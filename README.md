# PlantMCP

_(Still in early development, I'm testing functionality and tools, feedbacks are welcome)_

MCP Server for PlantUML - Fully based on Java MCP SDK and PlantUML library

## Quick Start

### With Java 25

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

### With Docker

No Java required. Build the image and point your MCP client to it:

```bash
docker build -t plantmcp .
```

```json
{
  "mcpServers": {
    "PlantMCP": {
      "type": "local",
      "command": "docker",
      "args": ["run", "--rm", "-i", "plantmcp"]
    }
  }
}
```

#### File I/O inside a container

When running in Docker, `/data` is your project directory — the host directory you mount via `-v`. All file operations (`validate` with `path`, `render`) are rooted there. Use just the **filename** for `path` parameters (e.g. `diagram.puml`, `output.svg`) — not absolute or relative paths with directories. Mount your working directory to `/data`:

```json
{
  "mcpServers": {
    "PlantMCP": {
      "type": "local",
      "command": "docker",
      "args": ["run", "--rm", "-i", "-v", ".:/data", "plantmcp"]
    }
  }
}
```

Place your `.puml` input files in the mounted directory before calling `validate` or `render`. Rendered SVG files will appear in the same directory.

_Tested on GitHub Copilot_

## Tools

| Tool       | Input                                        | Output                                                           |
|------------|----------------------------------------------|------------------------------------------------------------------|
| `validate` | `data`: PlantUML source OR `path`: file path | `"Schema is valid"` or syntax errors                             |
| `encode`   | `data`: PlantUML source                      | Encoded string (usable in `plantuml.com/plantuml/uml/<encoded>`) |
| `decode`   | `data`: Encoded string                       | PlantUML source                                                  |
| `render`   | `data`: PlantUML source, `path`: output path | SVG written to disk; returns confirmation with absolute path     |

`validate` accepts either `data` (PlantUML source text) or `path` (file path) — exactly one must be provided. `encode`/`decode`/`render` accept a `data` string parameter. `render` also accepts a `path` parameter. All return an `isError` flag on failure.

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
