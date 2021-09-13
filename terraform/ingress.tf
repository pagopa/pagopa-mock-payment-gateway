resource "kubernetes_ingress" "ingress" {
  metadata {
    name      = "pm-cloud-mock-ingress-service"
    namespace = kubernetes_namespace.pmnamespace.metadata[0].name
    annotations = {
      "nginx.ingress.kubernetes.io/rewrite-target" = "/$1"
    }
  }

  spec {
    rule {
      http {
        path {
          backend {
            service_name = "pagopapmcloudmock"
            service_port = 8080
          }

          path = "/pagopapmcloudmock/(.*)"
        }
      }
    }
  }
}