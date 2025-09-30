# Agentic AI Incident Automation Workflow

This project is a lightweight, AI-powered agent designed to automate the initial stages of IT incident response. It listens for incident alerts, uses a Large Language Model (LLM) to classify and enrich the data, integrates with Slack and GitHub to coordinate the response, and logs all incident details to a database. The core mission is to reduce the operational overhead (or "TAX") of incident management.

---

## ⚙️ Architecture

The application is built on a modular, service-oriented architecture powered by **Spring AI**.  
The workflow is entirely **event-driven**, starting from a single API call.

### 🔹 Components

1. **REST API Endpoint (`Incident.java`)**  
   A simple Spring web controller that exposes a `/incident` endpoint.  
   This is the entry point for all incoming incident alerts.

2. **Incident Service (`IncidentService.java`)**  
   The orchestrator that receives the raw alert. It runs asynchronously (`@Async`) to provide an immediate API response while the AI agent processes the request in the background.

3. **AI Agent Core (`Classify.java`)**  
   The brain of the operation. It uses Spring AI to communicate with an LLM (e.g., Qwen2).  
   It takes the user's prompt, combines it with a system prompt and context from the vector store, and decides which tools to call to execute the workflow.

4. **Vector Store KB (`IngestionService.java`)**  
   An in-memory knowledge base for **Retrieval-Augmented Generation (RAG)**.  
   On startup, it loads a predefined set of incident types and remediation steps into a vector store, allowing the AI agent to retrieve relevant solutions for new incidents.

5. **Tools (`Tools.java`)**  
   A collection of functions the AI Agent can execute. Each tool is a plain Java method annotated with `@Tool`, allowing the LLM to understand its purpose and parameters.  
   Includes:
    - Creating Slack channels
    - Posting GitHub issues
    - Saving the final record to the database

6. **Integrations (`Slack.java`, `Issue.java`)**  
   Low-level clients that handle the direct HTTP API communication with external services like **Slack** and **GitHub**.

---

## 🚀 Setup and Configuration

### ✅ Prerequisites

- **Java JDK 21** or higher
- **Docker** and Docker Compose (for running the database and LLM)
- **Ollama** (for serving the language model locally) → [Installation Guide](https://ollama.com/)

---

### 1. Database Setup

The project uses **PostgreSQL**. Run the following SQL script to create the necessary table and custom types:

```sql
-- Create ENUM types for structured data
CREATE TYPE incident_severity AS ENUM ('Low', 'Medium', 'High', 'Critical');
CREATE TYPE incident_status AS ENUM ('New', 'In Progress', 'Resolved', 'Closed');

-- Create the main table to store incident details
CREATE TABLE incidents_record (
    id SERIAL PRIMARY KEY,
    original_prompt TEXT NOT NULL,
    summary TEXT,
    severity incident_severity,
    suggested_remediation TEXT,
    status incident_status DEFAULT 'New',
    slack_channel_id VARCHAR(50),
    github_issue_url VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE incidents (
    id SERIAL PRIMARY KEY,
    issue_count INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO incidents (id, issue_count)
VALUES (1, 1)
```

---

### 2. Ollama LLM Setup

1. After installing Ollama, pull the language model:
   ```bash
   ollama pull qwen3:8B
   ```

2. Ensure the Ollama server is running:
   ```bash
   ollama list
   ```

---

### 3. Environment Configuration

Set environment variables in your shell or in `application.properties`:

```properties
# Spring AI - Ollama Configuration
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=qwen3:8B
spring.ai.ollama.init.timeout=5m
spring.ai.ollama.init.pull-model-strategy=always

# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/xlbizvectordb
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

# VectorStore Properties
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.dimensions=1024
spring.ai.vectorstore.pgvector.distance-type=cosine_distance
spring.ai.vectorstore.pgvector.index-type=hnsw

# API Tokens (in OS Environment)
SLACK_BOT_TOKEN=xoxb-your-slack-bot-token
GITHUB_TOKEN=ghp_your-github-personal-access-token
```

---

### 4. Build and Run

1. Clone the repository:
   ```bash
   git clone https://github.com/krashkanter/XLBiz-Project.git
   cd XLBiz-Project
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The app will start at **`http://localhost:8080`**

---

## 📡 Usage

Trigger an incident by sending a **POST** request to the `/incident` endpoint with a plain text payload.

**Example using cURL:**

```bash
curl -X POST   http://localhost:8080/incident   -H 'Content-Type: text/plain'   -d 'CRITICAL: The primary database server pg-prod-01 has disk usage at 93% on the /var/lib/postgresql volume. Immediate action is required to prevent downtime.'
```

The API immediately returns **`200 OK`**, while processing continues asynchronously.  
Check logs, your **Slack workspace**, and **GitHub repository** to see the agent’s activity.

---

## ⚖️ Trade-offs and Assumptions

- **LLM Reliability**  
  Effectiveness depends on the reasoning of the chosen LLM (`qwen3:8B`). Larger models may be more reliable but require more resources.

- **Input Format**  
  API accepts plain `text/plain` for flexibility, at the cost of no strict schema. A `JSON` payload would enforce structure.

- **Hardcoded Values**  
  Some values are hardcoded (e.g., default Slack users, GitHub repo).  
  In production, these should be configurable or dynamically resolved.

- **Error Handling**  
  Currently basic. Production-grade would require retries, DLQs, and better exception handling.

- **Idempotency**  
  Not idempotent. Sending the same request multiple times creates duplicate incidents, Slack channels, and GitHub issues.
