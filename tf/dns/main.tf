resource "google_dns_managed_zone" "smilefjes_dns_zone" {
  name = "smilefjes-mattilsynet-no-dns-zone"
  dns_name = "smilefjes.mattilsynet.no."
  project = var.project_id
  description = "Smilefjes"
  dnssec_config {
    state = "on"
  }
}

resource "google_dns_record_set" "smilefjes" {
  name = "${resource.google_dns_managed_zone.smilefjes_dns_zone.dns_name}"
  managed_zone = resource.google_dns_managed_zone.smilefjes_dns_zone.name
  type = "A"
  ttl = 300
  project = var.project_id
  rrdatas = ["34.36.126.51"]
}

resource "google_dns_record_set" "smilefjes-ipv6" {
  name = "${resource.google_dns_managed_zone.smilefjes_dns_zone.dns_name}"
  managed_zone = resource.google_dns_managed_zone.smilefjes_dns_zone.name
  type = "AAAA"
  ttl = 300
  project = var.project_id
  rrdatas = ["2600:1901:0:bdf0::"]
}
