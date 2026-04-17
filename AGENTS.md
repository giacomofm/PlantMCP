# AGENTS.md — PlantUML MCP Server

## Project Overview

**plantmcp** is an MCP (Model Context Protocol) server that enables AI agents to validate, encode, and decode PlantUML diagrams. Any MCP-compatible client can connect over stdio and use these tools programmatically.

### Architecture

```
Main → ServerMcpApp → McpSyncServer (stdio transport)
                          ├── ValidationMcpTool  (validate PlantUML source)
                          ├── EncodeMcpTool      (source → encoded string)
                          └── DecodeMcpTool      (encoded string → source)

All tools extend CustomMcpTool → delegates to PlantEngine
```

**Packages:**

| Package          | Purpose                                      |
|------------------|----------------------------------------------|
| `plantmcp`       | Entry point (`Main`)                         |
| `plantmcp.mcp`   | MCP server, tool base class, tool impls      |
| `plantmcp.plant` | `PlantEngine` — wraps PlantUML library calls |
| `plantmcp.cli`   | CLI interface (validate/encode/decode)       |

**Key classes:**

| Class               | Role                                                           |
|---------------------|----------------------------------------------------------------|
| `ServerMcpApp`      | Bootstraps `McpSyncServer`, registers all tools, starts stdio  |
| `CustomMcpTool`     | Abstract base — provides schema, logging, `PlantEngine` access |
| `ValidationMcpTool` | MCP tool: validates PlantUML source for syntax errors          |
| `EncodeMcpTool`     | MCP tool: encodes PlantUML source to shareable string          |
| `DecodeMcpTool`     | MCP tool: decodes encoded string back to PlantUML source       |
| `PlantEngine`       | Wraps PlantUML library (`getErrors`, `encode`, `decode`)       |

### Tech Stack

- **Language**: Java 25
- **Build**: Maven via wrapper (`./mvnw` or `mvnw.cmd` — no global Maven required)
- **MCP SDK**: `io.modelcontextprotocol.sdk` v1.1.1 (Java SDK with Jackson 3 JSON mapper)
- **PlantUML**: `net.sourceforge.plantuml:plantuml-mit` v1.2026.2
- **Logging**: SLF4J
- **Testing**: JUnit 6 (Jupiter) RC3

### Build & Test Commands

```bash
# Build
./mvnw compile                # mvnw.cmd on Windows

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest="plantmcp.mcp.EncodeMcpToolTest"

# Package
./mvnw package
```

---

## MCP Tools

All tools accept a single `data` parameter (required, type `string`). All return `McpSchema.CallToolResult` with `isError` flag.

### `validation` — Validate PlantUML source

| Input                          | Output (success)    | Output (error)                    |
|--------------------------------|---------------------|-----------------------------------|
| `data`: PlantUML source string | `"Schema is valid"` | List of syntax errors from parser |

**Error handling:**
- Non-string / null input → `isError: true`, `"Invalid input data. Expected a string."`
- Empty string → `isError: true`, `"No diagram found in source"`
- Parse exception → `isError: true`, `"An error occurred during validation: <message>"`

### `encode` — Encode PlantUML source to shareable string

| Input                          | Output (success)                         |
|--------------------------------|------------------------------------------|
| `data`: PlantUML source string | Encoded string (Deflate + custom base64) |

Encoded string usable in PlantUML server URLs: `https://www.plantuml.com/plantuml/uml/<encoded>`

**Error handling:**
- Non-string / null input → `isError: true`, `"Invalid input data. Expected a string."`
- Encoding failure → `isError: true`, `"An error occurred during encoding: <message>"`

### `decode` — Decode encoded string back to PlantUML source

| Input                           | Output (success)             |
|---------------------------------|------------------------------|
| `data`: PlantUML encoded string | Decoded PlantUML source text |

**Error handling:**
- Non-string / null input → `isError: true`, `"Invalid input data. Expected a string."`
- Invalid encoded string → `isError: true`, `"An error occurred during decoding: <message>"`

---

## CLI Interface

CLI operations exist in `plantmcp.cli` package. Same 3 operations (validate, encode, decode) but file-based.

### Exit Codes

| Code | Meaning                                        |
|------|------------------------------------------------|
| 0    | Success                                        |
| 1    | Internal error (unexpected exception)          |
| 2    | User error (invalid arguments, file not found) |
| 3    | Validation-specific: diagram has syntax errors |

### CLI Usage

```bash
plantmcp validate <path>           # validate .puml file
plantmcp encode <path>             # encode .puml file → encoded string
plantmcp decode <encoded-string>   # decode string → PlantUML source
```

---

## Code Conventions

- **Java version**: 25
- **No external CLI frameworks** — manual argument parsing
- **Test-all**: all features must have unit tests with good coverage
- **Immutable data where possible** — prefer records for data carriers
- **Minimal dependencies** — PlantUML, MCP SDK, JUnit, SLF4J only
- Ask permissions before changing `pom.xml`
- Use `./mvnw` (or `mvnw.cmd`) for all build/test commands
