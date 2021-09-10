resource "kubernetes_secret" "registry-auth-secret-cloud-mock" {
  metadata {
    name = "cloud-mock-registry-auth"
    namespace = kubernetes_namespace.pmnamespace.metadata[0].name

  }

  data = {
    ".dockerconfigjson" = <<DOCKER
{
  "auths": {
    "${var.registry_server}": {
      "auth": "${base64encode("${var.registry_username}:${var.registry_password}")}"
    }
  }
}
DOCKER
  }

  type = "kubernetes.io/dockerconfigjson"
}