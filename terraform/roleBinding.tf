resource "kubernetes_role_binding" "cloudmockrolebinding" {
  metadata {
    name      = "pm-cloud-mock-devops-deployment"
    namespace = kubernetes_namespace.pmnamespace.metadata[0].name
  }
  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "Role"
    name      = kubernetes_namespace.pmnamespace.metadata[0].name
  }
  subject {
    kind      = "ServiceAccount"
    name      = kubernetes_service_account.pmserviceaccount.metadata[0].name
    namespace = kubernetes_role.pmserviceaccountrole.metadata[0].name
  }
  depends_on = [
    kubernetes_service_account.pmserviceaccount,
    kubernetes_namespace.pmnamespace,
    kubernetes_role.pmserviceaccountrole
  ]
}