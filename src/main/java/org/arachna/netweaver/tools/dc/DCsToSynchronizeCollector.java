/**
 *
 */
package org.arachna.netweaver.tools.dc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPartReference;

/**
 * Collector for dependencies (in archive mode) of development components that shall be built during the current job run.
 *
 * The resultant set of dependencies will not contain development components that are provided by SAP when those components are already on
 * disk (i.e. already synchronized to the workspace). These components have another life cycle than components that may be development in
 * other compartments (or other tracks) and are updated only during a service pack upgrade.
 *
 * @author Dirk Weigenand
 */
public class DCsToSynchronizeCollector {
    /**
     * names of compartments provided by SAP (i.e. imported into the CBS on track creation).
     */
    private final Set<String> sapCompartments = new LinkedHashSet<String>(Arrays.asList("sap.com_ADSSAP_1", "sap.com_AJAXRUNTIME_1",
        "sap.com_BASETABLES_1", "sap.com_BIBASEB_1", "sap.com_BIBASEE_1", "sap.com_BIBASES_1", "sap.com_BIIBC_1", "sap.com_BIREPPLAN_1",
        "sap.com_BIUDI_1", "sap.com_BIWDALV_1", "sap.com_BIWDEXT_1", "sap.com_BIWEBAPP_1", "sap.com_BPEMBASE_1", "sap.com_BPEMBASIS_1",
        "sap.com_BPEMBUILDT_1", "sap.com_BPEMCOLLAB_1", "sap.com_BPEMCONTENT_1", "sap.com_BPEMCORE_1", "sap.com_BPEMCUUI_1",
        "sap.com_BPEMFACADE_1", "sap.com_BPEMHIM_1", "sap.com_BPEMMM_1", "sap.com_BPEMMON_1", "sap.com_BPEMPP_1", "sap.com_BPEMWDUI_1",
        "sap.com_BRMSBASE_1", "sap.com_BRMSBUILDT_1", "sap.com_BRMSCORE_1", "sap.com_BRMSFACADE_1", "sap.com_BRMSMON_1",
        "sap.com_BRMSWDUI_1", "sap.com_CAF_1", "sap.com_CAFMF_1", "sap.com_CAFUI_1", "sap.com_CERAEDM_1", "sap.com_CFGZA_1",
        "sap.com_CFGZACE_1", "sap.com_COLLABADP_1", "sap.com_COMPBUILDT_1", "sap.com_CORETOOLS_1", "sap.com_CUBASEJAVA_1",
        "sap.com_CUBASEWD_1", "sap.com_CUBASEWDEXT_1", "sap.com_CUWD4VCADPT_1", "sap.com_DATAMAPPING_1", "sap.com_DICBS_1",
        "sap.com_DICLIENTS_1", "sap.com_DICMS_1", "sap.com_DIDTR_1", "sap.com_DINTY_1", "sap.com_ECMADMIN_1", "sap.com_ECMAPPS_1",
        "sap.com_ECMCORE_1", "sap.com_ECMJEECOMP_1", "sap.com_ECMSTORE_1", "sap.com_ENGFACADE_1", "sap.com_ENGINEAPI_1",
        "sap.com_EPADMIN_1", "sap.com_EPAPPSEXT_1", "sap.com_EPBASIS_1", "sap.com_EPBASISAPI_1", "sap.com_EPBUILDT_1",
        "sap.com_EPCONNECTIVITY_1", "sap.com_EPCONNECTIVITYEXT_1", "sap.com_EPMODELING_1", "sap.com_EPRUNTIME_1", "sap.com_EPWPC_1",
        "sap.com_ESCONFBUILDT_1", "sap.com_ESIUI_1", "sap.com_ESMPBUILDT_1", "sap.com_ESPFRAMEWORK_1", "sap.com_ESREGBASIC_1",
        "sap.com_ESREGSERVICES_1", "sap.com_ETPRJSCHEDULER_1", "sap.com_FPINFRA_1", "sap.com_FRAMEWORK_1", "sap.com_FRAMEWORKEXT_1",
        "sap.com_GPCORE_1", "sap.com_GPPP_1", "sap.com_GPVC_1", "sap.com_GROUPWARE_1", "sap.com_IDEUPDSITETOOLS_1", "sap.com_J2EEAPPS_1",
        "sap.com_J2EEFRMW_1", "sap.com_JSPM_1", "sap.com_JWF_1", "sap.com_KMCBC_1", "sap.com_KMCCM_1", "sap.com_KMCCOLL_1",
        "sap.com_KMCWPC_1", "sap.com_KMKWJIKS_1", "sap.com_LMCFG_1", "sap.com_LMCORE_1", "sap.com_LMCTC_1", "sap.com_LMCTS_1",
        "sap.com_LMCTSUI_1", "sap.com_LMMODELBASE_1", "sap.com_LMMODELNW_1", "sap.com_LMNWABASICAPPS_1", "sap.com_LMNWABASICCOMP_1",
        "sap.com_LMNWABASICMBEAN_1", "sap.com_LMNWACDP_1", "sap.com_LMNWATOOLS_1", "sap.com_LMNWAUIFRMRK_1", "sap.com_LMPORTAL_1",
        "sap.com_LMSLD_1", "sap.com_LMTOOLS_1", "sap.com_MESSAGING_1", "sap.com_MMRSERVER_1", "sap.com_MOINBUILDT_1", "sap.com_NETPDK_1",
        "sap.com_NWTEC_1", "sap.com_PISCPBUILDT_1", "sap.com_PISCPEXT_1", "sap.com_RTC_1", "sap.com_RTCSTREAM_1", "sap.com_SAPBUILDT_1",
        "sap.com_SAPNWDEMO_1", "sap.com_SAPXI3RDPARTY_1", "sap.com_SAPXIADMIN_1", "sap.com_SAPXIAF_1", "sap.com_SAPXICONS_1",
        "sap.com_SAPXIESR_1", "sap.com_SAPXIGUILIB_1", "sap.com_SAPXITOOL_1", "sap.com_SEACORE_1", "sap.com_SEAFACADE_1",
        "sap.com_SEAUI_1", "sap.com_SECURITYEXT_1", "sap.com_SERVERCORE_1", "sap.com_SERVICECOMP_1", "sap.com_SOAMON_1",
        "sap.com_SOAMONBASIC_1", "sap.com_SRUI_1", "sap.com_SWLIFECYCL_1", "sap.com_THLCORE_1", "sap.com_TMWLUI_1", "sap.com_UDDI_1",
        "sap.com_UKMSJAVA_1", "sap.com_UMEADMIN_1", "sap.com_UWLJWF_1", "sap.com_VC70RUNTIME_1", "sap.com_VCBASE_1", "sap.com_VCCORERT_1",
        "sap.com_VCFRAMEWORK_1", "sap.com_VCFREESTYLEKIT_1", "sap.com_VCKITBI_1", "sap.com_VOICERT_1", "sap.com_VOICEVC_1",
        "sap.com_VTPBUILDT_1", "sap.com_WDADOBE_1", "sap.com_WDAPPS_1", "sap.com_WDEXTENSIONS_1", "sap.com_WDFLEX_1",
        "sap.com_WDRUNTIME_1", "sap.com_WDRUNTIMEEXT_1", "sap.com_WSRM_1", "sap.com_XICNTSAPBASIS_1"));

    /**
     * helper class to determine the location of a development component in the workspace.
     */
    private final AntHelper antHelper;

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Collector for development components (in archive state) to synchronize.
     *
     * The collector determines whether a used development component is one provided by SAP. When it is already on disk it will not be added
     * to the list of development components that should be synchronized. Development components not provided by SAP i.e. self developed DCs
     * are synchronized since their development life cycle is usually much shorter than updates to SAP components.
     *
     * @param dcFactory
     *            the development component registry used to translate DC references to their DC counterparts.
     * @param antHelper
     *            helper component used to determine the path of a DC in the Jenkins workspace.
     */
    DCsToSynchronizeCollector(final DevelopmentComponentFactory dcFactory, final AntHelper antHelper) {
        this.dcFactory = dcFactory;
        this.antHelper = antHelper;
    }

    /**
     * Determine whether the given DC is in archive state.
     *
     * @param usedDC
     *            used DC to be inspected. May be <code>null</code> since DC references might be deleted or may point to non existent DCs.
     * @return <code>true</code>, when the given DC is not <code>null</code> and its compartment is in archive state, <code>false</code>
     *         otherwise.
     */
    private boolean isUsedDCinArchiveState(final DevelopmentComponent usedDC) {
        boolean result = false;

        if (usedDC != null) {
            final Compartment compartment = usedDC.getCompartment();

            if (compartment != null) {
                result = compartment.isArchiveState();
            }
            else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, String.format("%s has no compartment!", usedDC.toString()));
            }
        }

        return result;
    }

    /**
     * Determine the development components that should be synchronized as dependencies for the given collection of development components.
     *
     * Development components that are provided by SAP and are already on disk are excluded from the synchronization as a speed
     * optimization.
     *
     * @param components
     *            development components to determine dependencies that shall be synchronized.
     * @return a list of development that the given list of development components depends on for building.
     */
    public List<DevelopmentComponent> execute(final Collection<DevelopmentComponent> components) {
        final List<DevelopmentComponent> dcsToSynchronize = new ArrayList<DevelopmentComponent>();

        for (final DevelopmentComponent usedDC : getUsedDCs(getUniquePublicPartReferences(components))) {
            // development components provided by SAP
            if (isSAPComponent(usedDC)) {
                // should only be synchronized when not already on disk.
                if (!isAlreadySynchronized(usedDC)) {
                    dcsToSynchronize.add(usedDC);
                }
            }
            else {
                dcsToSynchronize.add(usedDC);
            }
        }

        return dcsToSynchronize;
    }

    /**
     * Loop through the given development components and determine which development components they depend on.
     *
     * There may be several public parts for one development component depended on returned here.
     *
     * @param components
     *            collection of development components for which to determine dependencies (i.e. references to public parts of other
     *            development components).
     * @return collection of references to public parts of development components the given development components depend on.
     */
    private Collection<PublicPartReference> getUniquePublicPartReferences(final Collection<DevelopmentComponent> components) {
        final Collection<PublicPartReference> references = new HashSet<PublicPartReference>();

        for (final DevelopmentComponent component : components) {
            for (final PublicPartReference reference : component.getUsedDevelopmentComponents()) {
                references.add(reference);
            }
        }

        return references;
    }

    /**
     * Determine the set of development components referenced by the given collection of public part references.
     *
     * The collection of returned development components does not contain duplicates.
     *
     * @param references
     *            references to public parts of development components.
     * @return a collection of development components referenced by the given collection of public part references.
     */
    private Collection<DevelopmentComponent> getUsedDCs(final Collection<PublicPartReference> references) {
        final Collection<DevelopmentComponent> usedDCs = new HashSet<DevelopmentComponent>();

        for (final PublicPartReference reference : references) {
            final DevelopmentComponent usedDC = dcFactory.get(reference);

            if (isUsedDCinArchiveState(usedDC)) {
                usedDCs.add(usedDC);
            }
        }

        return usedDCs;
    }

    /**
     * Determine whether the given component is already on disk.
     *
     * @param component
     *            the development component to check whether it was synchronized already.
     * @return <code>true</code> when the development component descriptor (<code>.dcdef</code>) can be found in the workspace,
     *         <code>false</code> otherwise.
     */
    boolean isAlreadySynchronized(final DevelopmentComponent component) {
        final File dcDef = new File(antHelper.getBaseLocation(component), ".dcdef");

        return dcDef.exists() && dcDef.isFile();
    }

    /**
     * Determines whether the given development component is provided by SAP (i.e. its containing compartment is one of those listed in
     * {@see #sapCompartments}.
     *
     * @param component
     *            development component that should be tested for being supplied by SAP (its compartment is in a predefined list of
     *            compartment names).
     * @return <code>true</code> when the compartment name is in the list of compartments supplied by SAP with a NetWeaver installation,
     *         <code>false</code> otherwise.
     */
    boolean isSAPComponent(final DevelopmentComponent component) {
        return sapCompartments.contains(component.getCompartment().getName());
    }
}
