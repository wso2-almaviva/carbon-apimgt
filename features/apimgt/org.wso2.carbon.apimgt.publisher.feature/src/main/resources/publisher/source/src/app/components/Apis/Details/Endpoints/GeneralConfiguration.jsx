/**
 * Copyright (c)  WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect, useState } from 'react';
import {
    ExpansionPanel,
    ExpansionPanelDetails,
    ExpansionPanelSummary,
    Grid,
    Typography,
    withStyles,
} from '@material-ui/core';
import PropTypes from 'prop-types';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import { FormattedMessage, injectIntl } from 'react-intl';
import Certificates from './GeneralConfiguration/Certificates';
import API from '../../../../data/api'; // TODO: Use webpack aliases instead of relative paths ~tmkb
import Alert from '../../../Shared/Alert';
import { endpointsToList } from './endpointUtils';

const styles = (theme) => ({
    configHeaderContainer: {
        display: 'flex',
        justifyContent: 'space-between',
    },
    generalConfigContent: {
        boxShadow: 'inset -1px 2px 3px 0px #c3c3c3',
    },
    secondaryHeading: {
        fontSize: theme.typography.pxToRem(15),
        color: theme.palette.text.secondary,
        display: 'flex',
    },
    heading: {
        fontSize: theme.typography.pxToRem(15),
        flexBasis: '33.33%',
        flexShrink: 0,
        fontWeight: '900',
    },
    endpointConfigSection: {
        padding: '10px',
    },
    generalConfigPanel: {
        width: '100%',
    },
    securityHeading: {
        fontWeight: 600,
    },
    sandboxEndpointSwitch: {
        marginLeft: theme.spacing(2),
    },
});

/**
 * The component which holds the general configurations of the endpoints.
 *
 * @param {any} props The input properties to the component
 * @returns {any} The HTML representation of the component.
 * */
function GeneralConfiguration(props) {
    const {
        intl,
        epConfig,
        endpointType,
        classes,
    } = props;
    const [isConfigExpanded, setConfigExpand] = useState(false);
    const [endpointCertificates, setEndpointCertificates] = useState([]);
    const [aliasList, setAliasList] = useState([]);

    /**
     * Method to upload the certificate content by calling the rest api.
     * */
    const saveCertificate = (certificate, endpoint, alias) => {
        return API.addCertificate(certificate, endpoint, alias)
            .then((resp) => {
                if (resp.status === 201) {
                    Alert.info(intl.formatMessage({
                        id: 'Apis.Details.Endpoints.GeneralConfiguration.Certificates.certificate.add.success',
                        defaultMessage: 'Certificate added successfully',
                    }));
                    const tmpCertificates = [...endpointCertificates];
                    tmpCertificates.push({
                        alias: resp.obj.alias,
                        endpoint: resp.obj.endpoint,
                    });
                    setEndpointCertificates(tmpCertificates);
                }
            })
            .catch((err) => {
                console.error(err.message);
                if (err.message === 'Conflict') {
                    Alert.error(intl.formatMessage({
                        id: 'Apis.Details.Endpoints.GeneralConfiguration.Certificates.certificate.alias.exist',
                        defaultMessage: 'Adding Certificate Failed. Certificate Alias Exists.',
                    }));
                } else if (err.response) {
                    Alert.error(err.response.body.description);
                } else {
                    Alert.error(intl.formatMessage({
                        id: 'Apis.Details.Endpoints.GeneralConfiguration.Certificates.certificate.error',
                        defaultMessage: 'Something went wrong while adding the certificate.',
                    }));
                }
            });
    };
    /**
     * Method to delete the selected certificate.
     *
     * @param {string} alias The alias of the certificate to be deleted.
     * */
    const deleteCertificate = (alias) => {
        return API.deleteEndpointCertificate(alias)
            .then((resp) => {
                setEndpointCertificates(() => {
                    if (resp.status === 200) {
                        return endpointCertificates.filter((cert) => {
                            return cert.alias !== alias;
                        });
                    } else {
                        return -1;
                    }
                });
                Alert.info(intl.formatMessage({
                    id: 'Apis.Details.Endpoints.GeneralConfiguration.Certificates.certificate.delete.success',
                    defaultMessage: 'Certificate Deleted Successfully',
                }));
            })
            .catch((err) => {
                console.log(err);
                Alert.info(intl.formatMessage({
                    id: 'Apis.Details.Endpoints.GeneralConfiguration.Certificates.certificate.delete.error',
                    defaultMessage: 'Error Deleting Certificate',
                }));
            });
    };

    // Get the certificates from backend.
    useEffect(() => {
        API.getEndpointCertificates()
            .then((resp) => {
                const { certificates } = resp.obj;
                const endpoints = endpointsToList(epConfig);
                const aliases = [];
                const filteredCertificates = certificates.filter((cert) => {
                    aliases.push(cert.alias);
                    for (const endpoint of endpoints) {
                        if (endpoint && endpoint.url.indexOf(cert.endpoint) !== -1) {
                            return true;
                        }
                    }
                    return false;
                });
                setEndpointCertificates(filteredCertificates);
                setAliasList(aliases);
            })
            .catch((err) => {
                console.error(err);
                setEndpointCertificates([]);
            });
    }, []);

    return (
        <>
            <ExpansionPanel
                expanded={isConfigExpanded}
                onChange={() => setConfigExpand(!isConfigExpanded)}
                className={classes.generalConfigPanel}
            >
                <ExpansionPanelSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls='panel1bh-content'
                    id='panel1bh-header'
                    className={classes.configHeaderContainer}
                >
                    {endpointType.key === 'default' || endpointType.key === 'awslambda' ? (
                        <div />
                    ) : (
                        <Typography
                            className={classes.secondaryHeading}
                            hidden={endpointType.key === 'default' || endpointType.key === 'awslambda'}
                        >
                            <FormattedMessage
                                id='Apis.Details.Endpoints.GeneralConfiguration.certificates.sub.heading'
                                defaultMessage='Certificates'
                            />
                            :
                            {' '}
                            {endpointCertificates.length}
                        </Typography>
                    )}
                </ExpansionPanelSummary>
                <ExpansionPanelDetails className={classes.generalConfigContent}>
                    <Grid
                        container
                        className={classes.endpointConfigSection}
                        hidden={endpointType.key === 'default' || endpointType.key === 'awslambda'}
                    >
                        <Certificates
                            endpoints={endpointsToList(epConfig)}
                            certificates={endpointCertificates}
                            uploadCertificate={saveCertificate}
                            deleteCertificate={deleteCertificate}
                            aliasList={aliasList}
                        />
                    </Grid>
                </ExpansionPanelDetails>
            </ExpansionPanel>
        </>
    );
}

GeneralConfiguration.propTypes = {
    epConfig: PropTypes.shape({}).isRequired,
    endpointType: PropTypes.shape({}).isRequired,
    classes: PropTypes.shape({}).isRequired,
    intl: PropTypes.shape({}).isRequired,
};

export default injectIntl(withStyles(styles)(GeneralConfiguration));
