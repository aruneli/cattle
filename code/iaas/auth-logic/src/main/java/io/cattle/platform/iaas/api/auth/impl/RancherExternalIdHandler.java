package io.cattle.platform.iaas.api.auth.impl;

import io.cattle.platform.api.auth.ExternalId;
import io.cattle.platform.core.constants.ProjectConstants;
import io.cattle.platform.core.model.Account;
import io.cattle.platform.iaas.api.auth.ExternalIdHandler;
import io.cattle.platform.iaas.api.auth.dao.AuthDao;
import io.cattle.platform.object.ObjectManager;
import io.github.ibuildthecloud.gdapi.context.ApiContext;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

public class RancherExternalIdHandler implements ExternalIdHandler {

    @Inject
    AuthDao authDao;
    @Inject
    ObjectManager objectManager;

    @Override
    public ExternalId transform(ExternalId externalId) {
        if (externalId.getType().equalsIgnoreCase(ProjectConstants.RANCHER_ID)) {
            String accountId = ApiContext.getContext().getIdFormatter().parseId(externalId.getId());
            Account account = authDao.getAccountById(Long.valueOf(accountId));
            if (account != null){
                return  new ExternalId(String.valueOf(account.getId()), externalId.getType(), account.getName());
            }
        }
        return null;
    }

    @Override
    public ExternalId untransform(ExternalId externalId) {
        if (externalId.getType().equalsIgnoreCase(ProjectConstants.RANCHER_ID)) {
            Account account = authDao.getAccountById(Long.valueOf(externalId.getId()));
            if (account != null){
                String accountId = (String) ApiContext.getContext().getIdFormatter().formatId(objectManager.getType(Account.class), account.getId());
                return  new ExternalId(accountId, externalId.getType(), account.getName());
            }
        }
        return null;
    }

    @Override
    public Set<ExternalId> getExternalIds(Account account) {
        Set<ExternalId> externalIds = new HashSet<>();
        externalIds.add(new ExternalId(String.valueOf(account.getId()), ProjectConstants.RANCHER_ID, account.getName()));
        return externalIds;
    }
}
