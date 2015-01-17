Mobile Malware Scanning using ClamAV 
========
Smart phone application for Android devices for scanning files in mobile devices with an Open Source Antivirus solution.

Steps used by the application:
1. The Android application connects to a server.
2. Application sends the files to the server and invokes the ClamAV process.
3. ClamAv performs the scan of the files using ClamScan.
4. Once Scan is complete it generates a report on the scanned file.
5. Server then sends the scan report back to the user (Application).
