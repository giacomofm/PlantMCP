# PlantMCP

_(Still in early development, I'm testing functionality and tools, feedbacks are welcome)_

MCP Server for PlantUML - Fully based on Java MCP SDK and PlantUML library

## Quick Start

**You need Java 25 to run the server.**  
Download `plantmcp.jar` from the [latest release](https://github.com/giacomofm/PlantMCP/releases/tag/latest) and add it to your MCP client config:

```json
{
  "mcpServers": {
    "PlantMCP": {
      "type": "local",
      "command": "java",
      "args": ["-jar", "/path/to/plantmcp.jar"]
    }
  }
}
```
_Tested on GitHub Copilot_

## Tools

| Tool              | Input                                        | Output                                                           |
|-------------------|----------------------------------------------|------------------------------------------------------------------|
| `validate_source` | `data`: PlantUML source                      | `"Schema is valid"` or syntax errors                             |
| `validate_file`   | `path`: path to .puml file                   | `"Schema is valid"` or syntax errors                             |
| `encode`          | `data`: PlantUML source                      | Encoded string (usable in `plantuml.com/plantuml/uml/<encoded>`) |
| `decode`          | `data`: Encoded string                       | PlantUML source                                                  |
| `render`          | `data`: PlantUML source, `path`: output path | SVG written to disk; returns confirmation with absolute path     |

`validate_source` and `encode`/`decode`/`render` accept a `data` string parameter. `validate_file` and `render` also accept a `path` parameter. All return an `isError` flag on failure.

## CLI

```bash
plantmcp validate <path>                       # validate .puml file
plantmcp encode <path>                         # encode .puml → encoded string
plantmcp decode <encoded-string>               # decode string → PlantUML source
plantmcp render <input-path> <output-path>     # render .puml → SVG file
```

Exit codes: `0` success · `1` internal error · `2` user error · `3` validation failed

## Build

```bash
./mvnw test       # run tests
./mvnw package    # package jar
```

## Stack

- Java 25
- [MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk) v1.1.1
- [PlantUML MIT](https://plantuml.com) v1.2026.2
