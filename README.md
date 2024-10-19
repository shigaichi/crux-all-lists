# Chrome (CrUX) ALL Websites

This repository automates the process of downloading the [Chrome User Experience Report (CrUX)](https://developer.chrome.com/docs/crux) dataset on a monthly basis using GitHub Actions. The dataset is collected from real-world Chrome browser users and reflects the user experience for popular web pages. Each month, a CSV file containing the latest CrUX data is generated and attached to the release page of this repository.

This repository is inspired by [zakird/crux-top-lists](https://github.com/zakird/crux-top-lists), which collects and provides access to CrUX data for the top 1 million websites. While that repository focuses on top sites, I needed access to all CrUX data, not just the top 1 million. As a result, I created this repository to suit my needs and automate the collection of the full dataset.

## How It Works

1. Every month, a GitHub Action is triggered to download the latest CrUX data.
   * The CrUX data is retrieved from [BigQuery](https://developer.chrome.com/docs/crux/methodology/tools#tool-bigquery).
   * BigQuery releases the CrUX data on the second Tuesday of each month.
   * This GitHub Actions workflow is scheduled to run on the 15th of every month to download the newly available data.
2. The data is processed and saved as a `.csv.xz` file, a compressed CSV format to reduce file size. You can extract it using common tools like `xz` or `7-Zip`.
3. The CSV file is then automatically uploaded and attached to the monthly release on the Releases page.

## Data License

The CrUX dataset is made available under the [Creative Commons Attribution 4.0 International License](https://developer.chrome.com/docs/crux/methodology#license).
