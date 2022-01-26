/**
 * RUDI Portail
 */
package org.rudi.wso2.userstore.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.rudi.wso2.userstore.RudiUserStoreManager;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="custom.user.store.manager.dscomponent" immediate=true
 * @scr.reference name="user.realmservice.default"
 *                interface="org.wso2.carbon.user.core.service.RealmService"
 *                cardinality="1..1" policy="dynamic" bind="setRealmService"
 *                unbind="unsetRealmService"
 */
//@Component(name = "rudi.user.store.mgt.dscomponent", immediate = true)
public class RudiUserStoreMgtDSComponent {

    private static final Log LOGGER = LogFactory
            .getLog(RudiUserStoreMgtDSComponent.class);

    protected RealmService realmService;

    // @Activate
    protected void activate(ComponentContext ctxt) {
        LOGGER.info("RudiUserStoreManager bundle start activate...");
        RudiUserStoreManager customUserStoreManager = new RudiUserStoreManager();
        ctxt.getBundleContext().registerService(
                UserStoreManager.class.getName(), customUserStoreManager, null);
        LOGGER.info("RudiUserStoreManager bundle activated successfully.");
    }

    // @Deactivate
    protected void deactivate(ComponentContext ctxt) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Custom User Store Manager is deactivated ");
        }
    }

    // @Reference(name = "user.realmservice.default",
    // cardinality = ReferenceCardinality.MANDATORY,
    // policy = ReferencePolicy.DYNAMIC, unbind = "unsetRealmService")
    protected void setRealmService(RealmService rlmService) {
        realmService = rlmService;
    }

    protected void unsetRealmService(RealmService realmService) {
        this.realmService = null;
    }

}
