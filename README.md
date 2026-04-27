# PlantMCP

_Tested on GitHub Copilot w/ `Sonet`/`Opus` `4.6`_  
_(I'm still testing functionality and tools, feedbacks are welcome)_

**MCP Server for PlantUML - Fully based on Java MCP SDK and PlantUML library**  
PlantMCP exposes PlantUML as a set of MCP tools that any compatible AI agent can call directly: validate diagram syntax, encode/decode diagram source, and render SVG files to disk without leaving the agent workflow.

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

_!!WARN!! You have more restrictions on file I/O when running in container mode._

No Java required, get the latest image from [GitHub Packages](https://github.com/giacomofm/PlantMCP/pkgs/container/plantmcp) and add it to your MCP client config.

When running in Docker, I suggest you mount it with `-v .:/data` so that your project root directory is accessible to the tool.  
All file operations (`validate` with `path`, `render`) are rooted there for the moment.  
To configure your MCP client by setting `args` like this:

```json
{
  "mcpServers": {
    "PlantMCP": {
      "type": "local",
      "command": "docker",
      "args": [
        "run",
        "--rm",
        "-i",
        "-v", ".:/data",
        "ghcr.io/giacomofm/plantmcp:latest"
      ]
    }
  }
}
```

Place your `.puml` input files in the mounted directory before calling `validate` or `render`. Rendered SVG files will appear in the same directory.

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
