targetScope = 'subscription'

@minLength(1)
@maxLength(64)
@description('Name of the environment (used for resource naming)')
param environmentName string = 'supplychain'

@description('Primary location for all resources')
param location string = 'westus2'

@secure()
@description('Administrator password for MySQL Flexible Server')
param mysqlAdminPassword string

// Resource token for unique naming
var resourceToken = uniqueString(subscription().id, resourceGroup.id, location, environmentName)

// Resource names following az{prefix}{token} convention
var rgName = 'rg-${environmentName}'
var logAnalyticsName = 'azlog${resourceToken}'
var managedIdentityName = 'azmi${resourceToken}'
var containerRegistryName = 'azacr${resourceToken}'
var keyVaultName = 'azkv${resourceToken}'
var containerAppEnvName = 'azenv${resourceToken}'
var containerAppName = 'azca${resourceToken}'
var mysqlServerName = 'azsql${resourceToken}'
var serviceBusName = 'azsb${resourceToken}'

// Well-known role definition IDs
var acrPullRoleId = '7f951dda-4ed3-4680-a7ca-43fe172d538d'
var keyVaultSecretsOfficerRoleId = 'b86a8fe4-44ce-4948-aee5-eccb2c155cd7'
var serviceBusDataSenderRoleId = '69a216fc-b8fb-44d8-bc22-1f3c2cd27a39'
var serviceBusDataReceiverRoleId = '4f6d3b9b-027b-4f4c-9142-0e5a2a2247e0'

// Resource Group
resource resourceGroup 'Microsoft.Resources/resourceGroups@2024-03-01' = {
  name: rgName
  location: location
  tags: {
    'azd-env-name': environmentName
  }
}

// All resources deployed into the resource group
module resources 'resources.bicep' = {
  name: 'resources-deployment'
  scope: resourceGroup
  params: {
    location: location
    environmentName: environmentName
    logAnalyticsName: logAnalyticsName
    managedIdentityName: managedIdentityName
    containerRegistryName: containerRegistryName
    keyVaultName: keyVaultName
    containerAppEnvName: containerAppEnvName
    containerAppName: containerAppName
    mysqlServerName: mysqlServerName
    serviceBusName: serviceBusName
    mysqlAdminPassword: mysqlAdminPassword
    acrPullRoleId: acrPullRoleId
    keyVaultSecretsOfficerRoleId: keyVaultSecretsOfficerRoleId
    serviceBusDataSenderRoleId: serviceBusDataSenderRoleId
    serviceBusDataReceiverRoleId: serviceBusDataReceiverRoleId
  }
}

// Outputs
output RESOURCE_GROUP_NAME string = resourceGroup.name
output AZURE_CONTAINER_REGISTRY_NAME string = resources.outputs.containerRegistryName
output AZURE_CONTAINER_REGISTRY_ENDPOINT string = resources.outputs.containerRegistryLoginServer
output AZURE_CONTAINER_APP_NAME string = resources.outputs.containerAppName
output AZURE_CONTAINER_APP_FQDN string = resources.outputs.containerAppFqdn
output AZURE_CONTAINER_APPS_ENVIRONMENT_NAME string = resources.outputs.containerAppEnvName
output AZURE_KEY_VAULT_NAME string = resources.outputs.keyVaultName
output AZURE_KEY_VAULT_URI string = resources.outputs.keyVaultUri
output AZURE_MANAGED_IDENTITY_NAME string = resources.outputs.managedIdentityName
output AZURE_MANAGED_IDENTITY_CLIENT_ID string = resources.outputs.managedIdentityClientId
output AZURE_MYSQL_SERVER_NAME string = resources.outputs.mysqlServerName
output AZURE_MYSQL_FQDN string = resources.outputs.mysqlFqdn
output AZURE_SERVICE_BUS_NAMESPACE string = resources.outputs.serviceBusNamespace
output AZURE_LOG_ANALYTICS_WORKSPACE_NAME string = resources.outputs.logAnalyticsName
