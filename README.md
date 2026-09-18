# AdEnvi — Mobile Inventory System

AdEnvi is a mobile inventory management system I developed as a **final-term school project for my Mobile Development class**.

The main idea was to make it easier to keep track of inventory items using QR codes. When adding an item, the user enters its details and the application generates a unique QR code for that item. The QR code can then be printed and placed on the physical item.

When the item needs to be checked or updated later, the user can simply scan its QR code instead of manually searching for the item.

## How It Works

1. A user adds a new inventory item.
2. The user enters the item's details.
3. The application generates a unique QR code for the item.
4. The QR code can be printed and attached to the physical item.
5. When the item needs to be viewed or updated, the user scans its QR code.
6. The application retrieves the item's stored information.

## Features

* QR Code Generation — Generates a unique QR code for each inventory item.
* QR Code Scanning — Scan an item's QR code to quickly access its information.
* Inventory Management — Add, view, and update inventory item details.
* Item Image Capture — Capture and store an image of an inventory item.
* Voice Commands — Speak an item ID to retrieve its information without scanning the QR code.
* Mobile Interface — Designed as a native Android application.

## Voice Command Feature

One of the more ambitious parts of this project was the **voice command feature**.

Instead of scanning the QR code, the user can speak the item's ID and the application will search for and display the corresponding item details.

Looking back at the project, this feature was probably more ambitious than what was required for a final-term school project, but it was something I wanted to experiment with at the time. It was also one of the features I was particularly happy with because it gave me an early experience with combining different Android features into one application.

## Technologies Used

* Android Studio
* Java
* QR Code Library
* Android SpeechRecognizer
* Camera API
* Native Android development

## Project Purpose

AdEnvi was created as a **final-term project for my Mobile Development class**. The project was mainly a learning experience and allowed me to experiment with QR codes, voice recognition, camera functionality, and mobile inventory management.

It also reflects one of my earlier tendencies as a developer to make projects more ambitious than the basic requirements, which made the project more challenging but also more enjoyable to build.

## Project Status

**No Longer Running**

The project has been shut down. The database/backend that the application was connected to is no longer available, so the application can no longer properly connect to its data.

Because the application depends on that backend, I am not providing an APK for this project. The project is kept as a record of one of my school projects and the features I experimented with during my Mobile Development class.
