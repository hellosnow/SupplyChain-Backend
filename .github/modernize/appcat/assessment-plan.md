# Assessment Plan

## Project: SupplyChain-Backend

**Type:** Java / Spring Boot  
**Assessment Tool:** AppCAT (Azure Migrate application and code assessment)

## Targets

| Target | Description |
|--------|-------------|
| azure-aks | Azure Kubernetes Service |
| azure-appservice | Azure App Service |
| azure-container-apps | Azure Container Apps |

## Mode

`issue-only` — analyze source code to detect issues

## Steps

1. Run AppCAT static analysis against all targets
2. Generate `report.json` with issues and recommendations
3. Save versioned report to `.github/modernize/assessment/reports/report-{reportId}/`

