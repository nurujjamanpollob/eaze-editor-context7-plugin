# 🚀 Context7 Documentation Finder Plugin

A blazing-fast documentation lookup and search plugin for the Eaze Editor AI system, integrating Context7's capabilities natively via Context Model Protocol (MCP).

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Powered by Context7](https://img.shields.io/badge/Powered_by-Context7-orange.svg)](https://context7.com)

## ✨ Overview

The Context7 Documentation Finder plugin supercharges your AI coding assistant with real-time documentation, API references, and syntax tutorials across various modern frameworks! It lets the agent interact dynamically with the [Context7 API](https://context7.com) right inside the editor, so you never have to break your flow context-switching to a browser safely.

## 🌟 Features

- 🔍 **Real-time Documentation Search**: Search for documentation using the Context7 API.
- 📚 **Multi-library Support**: Rich support for React, Vue, Angular, Node.js, Python, Java, TypeScript, Go, Rust, C#, PHP, and more.
- 🤖 **MCP Integration**: Seamlessly hooks into the Eaze Editor AI Core using the Model Context Protocol.
- ⚡ **Auto-detect Library**: Automatically detect the framework based on IDE context or imports.
- ⚙️ **Configurable Settings**: Customize your search experience, request timeouts, and result limits.
- 🔒 **Secure Persistence**: Secure preferences handler persisting your configurations securely in the editor.

## 🚀 Installation

1. Build the plugin: `./gradlew build`
2. Install the resulting plugin JAR from `eaze-editor-plugin/build/libs/` to the Eaze Editor plugins page.
3. Restart the editor to allow the tool registry to load the MCP settings.

## 🛠️ Configuration

Access the plugin settings through the Eaze Editor settings panel:
- ✅ **Enable/disable** the plugin
- 🔑 **Configure Context7 API key** — *Get your API key at [context7.com](https://context7.com/)*
- 🎯 **Set preferred library** for default documentation search
- ⏱️ **Adjust limits** for maximum results and request timeouts
- 🪄 **Auto-lookup** toggle for code selection snippets

## 📂 Plugin Structure

```text
eaze-editor-context7-plugin/
├── core/
│   └── src/main/java...            # Context7 Java Client Logic
├── eaze-editor-plugin/             
│   ├── src/main/java/              # MCP Tools & Settings Handler Integration
│   └── src/main/resources/context7/
│       ├── settings.html           # Settings page UI
│       ├── about.html              # About page
│       ├── script.js               # Client-side JavaScript logic
│       └── tailwind.cdn.js         # CSS utilities
├── sdks/                           # Pre-bundled Eaze Editor SDKs required for compilation
└── build.gradle                    # Main build configurations
```

## Development

### Prerequisites

- Java 25 (or minimally 21+)
- Gradle
- Access to the Eaze Editor Plugin SDK (bundled in `sdks/` folder)

### Building

```bash
./gradlew build
```

### Testing

```bash
./gradlew test
```

## License

MIT License

## 👨‍💻 Credits & Authors
* **Author**: [Nurujjaman Pollob](https://github.com/nurujjamanpollob)
* **Platform**: Built for [Eaze Editor](https://eazeditor.com/) by the Eaze Editor Team.
