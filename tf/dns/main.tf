resource "google_dns_managed_zone" "smilefjes_dns_zone" {
  name = "smilefjes-mattilsynet-no-dns-zone"
  dns_name = "smilefjes.mattilsynet.no."
  project = var.project_id
  description = "Smilefjes"
  dnssec_config {
    state = "on"
  }
}
