terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 2.65"
    }

    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.4.1"
    }
  }

  required_version = ">= 0.14.9"
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

variable "registry_server" {
  type = string
}

variable "registry_username" {
  type = string
}

variable "registry_password" {
  type = string
}

resource "kubernetes_namespace" "pmnamespace" {
  metadata {
    name = "pm-cloud-mock"
  }
}

resource "kubernetes_service_account" "pmserviceaccount" {
  metadata {
    name      = "azure-devops-pm-cloud-mock"
    namespace = kubernetes_namespace.pmnamespace.metadata[0].name
  }
  depends_on = [
    kubernetes_namespace.pmnamespace
  ]
}

resource "kubernetes_role" "pmserviceaccountrole" {
  metadata {
    name      = "pm-cloud-mock"
    namespace = kubernetes_namespace.pmnamespace.metadata[0].name
  }

  rule {
    api_groups = ["", "extensions", "apps"]
    resources  = ["deployments", "replicasets", "pods", "services"]
    verbs      = ["get", "list", "watch", "create", "update", "patch", "delete"]
  }
  depends_on = [
    kubernetes_namespace.pmnamespace,
    kubernetes_service_account.pmserviceaccount
  ]
}

