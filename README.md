# BuildNotifier

<!-- Plugin description -->
BuildNotifier sends IDE balloon notifications and/or webhook calls when a build finishes in Rider.
Supports filtering by build duration and customizable webhook payload templates.
<!-- Plugin description end -->

## Features

- Balloon notification in the IDE when build succeeds or fails
- Optional webhook call on build completion (e.g. for Slack, WeChat Work, or any HTTP endpoint)
- Configurable payload template with variables: `${status}`, `${status_emoji}`, `${project}`, `${duration}`, `${duration_ms}`, `${errors}`, `${timestamp}`
- Option to notify only when build exceeds a configurable duration threshold

## Installation

- Manually:

  Download the latest release and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Configuration

Go to <kbd>Preferences</kbd> > <kbd>Tools</kbd> > <kbd>BuildNotifier</kbd> to configure:

- Enable/disable build notifications
- Show/hide balloon notifications in IDE
- Set a minimum build duration threshold for notifications
- Configure a webhook URL and payload template

---
Plugin based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).
