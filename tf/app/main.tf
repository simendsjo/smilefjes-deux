module "smilefjes-ui" {
  source = "git@github.com:Mattilsynet/map-tf-cloudrun?ref=v0.9.2"

  create_cloudrun_service_account_only = false
  service_name = "smilefjes"
  service_location = var.region
  service_project_id = var.project_id
  service_image = "gcr.io/cloudrun/hello"
  ignore_image = true

  replicas = {
    minScale = 0
    maxScale = 2
  }

  container_limits = {
    "cpu": "256m",
    "memory": "256Mi"
  }

  run_under_shared_lb = false
  allow_unauthenticated = true
  ingress = "internal-and-cloud-load-balancing"

  dedicated_lb = {
    managed_zone_name = "smilefjes-mattilsynet-no-dns-zone"
    fqdn = "smilefjes.mattilsynet.no"
    enable_ipv6 = true
  }
}

output "service" {
  value = "smilefjes"
}
