terraform {
  required_version = ">= 1.1.7"
  backend "gcs" {
    bucket = "tf-state-smilefjes-4614"
    prefix = "tf-dns"
  }
}

provider "google" {
  region = "europe-north1"
  impersonate_service_account = "tf-admin-sa@smilefjes-4614.iam.gserviceaccount.com"
}

provider "google-beta" {
  region = "europe-north1"
  impersonate_service_account = "tf-admin-sa@smilefjes-4614.iam.gserviceaccount.com"
}
