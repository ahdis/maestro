# XML Extraction Step

This step extracts data from XML files using XPath expressions. It allows users to specify the XML file path and the XPath query to retrieve specific elements or attributes from the XML structure.

## How it Works

Using two inputs properties, `xmlFile` and `xmlPath`, the XML Extraction step processes the provided XML content and extracts the data located at the specified XPath.

Possible results :
- `DONE`: The extraction was successful, and the data is accessible via the output property `extractedValue`.
- `UNDEFINED`: An error occurred during the extraction process, a message describing the error is available in the output property `failureMessage`.
- `PASSED`: NOT POSSIBLE for this step.
- `FAILED`: NOT POSSIBLE for this step.

## Examples of JSON Test Runs

### Simple XML Extraction

```json
{
  "testRunId": "XML Extraction Test Run",
  "test": {
    "id": "Simple XML Extraction",
    "name": "Simple XML Extraction",
    "steps": [
      {
        "name": "XML Extraction Step",
        "type": "XML_EXTRACTION",
        "properties": [
          {
            "name": "xmlFile",
            "type": "byteArray",
            "value": "${xmlInput}"
          },
          {
            "name": "xmlPath",
            "type": "string",
            "value": "/note/body"
          }
        ]
      }
    ]
  },
  "inputs": [
    {
      "name": "xmlInput",
      "type": "byteArray",
      "value": "PG5vdGU+Cjx0bz5Ub3ZlPC90bz4KPGZyb20+SmFuaTwvZnJvbT4KPGhlYWRpbmc+UmVtaW5kZXI8L2hlYWRpbmc+Cjxib2R5PkRvbid0IGZvcmdldCBtZSB0aGlzIHdlZWtlbmQhPC9ib2R5Pgo8L25vdGU+"
    }
  ]
}
```

### Simple XML Extraction + Validation step

```json
{
  "testRunId": "XML Extraction and Validation Test Run",
  "test": {
    "id": "XML Extraction and Validation",
    "name": "XML Extraction and Validation",
    "steps": [
      {
        "id": "extractionStep1",
        "name": "XML Extraction Step",
        "type": "XML_EXTRACTION",
        "properties": [
          {
            "name": "xmlFile",
            "type": "byteArray",
            "value": "${xmlInput}"
          },
          {
            "name": "xmlPath",
            "type": "string",
            "value": "/note/body"
          }
        ]
      },
      {
        "id": "validationStep2",
        "name": "Validation Step",
        "type": "VALIDATION",
        "properties": [
          {
            "name": "validationService",
            "type": "string",
            "value": "matchbox"
          },
          {
            "name": "validationProfile",
            "type": "string",
            "value": "https://profiles.ihe.net/RAD/IRA/StructureDefinition/diagnosticreportcontext|1.0.0"
          },
          {
            "name": "contentToValidate",
            "type": "byteArray",
            "value": "${extractionStep1.extractedValue}"
          }
        ]
      }
    ]
  },
  "inputs": [
    {
      "name": "xmlInput",
      "type": "byteArray",
      "value": "PG5vdGU+Cjx0bz5Ub3ZlPC90bz4KPGZyb20+SmFuaTwvZnJvbT4KPGhlYWRpbmc+UmVtaW5kZXI8L2hlYWRpbmc+Cjxib2R5PkRvbid0IGZvcmdldCBtZSB0aGlzIHdlZWtlbmQhPC9ib2R5Pgo8L25vdGU+"
    }
  ]
}
```

```json
{
  "testRunId": "XML Extraction and Validation Test Run",
  "test": {
    "id": "XML Extraction and Validation",
    "name": "XML Extraction and Validation",
    "steps": [
      {
        "id": "validationStep1",
        "name": "Validation Step",
        "type": "VALIDATION",
        "properties": [
          {
            "name": "validationService",
            "type": "string",
            "value": "matchbox"
          },
          {
            "name": "validationProfile",
            "type": "string",
            "value": "https://profiles.ihe.net/RAD/IRA/StructureDefinition/diagnosticreportcontext|1.0.0"
          },
          {
            "name": "contentToValidate",
            "type": "byteArray",
            "value": "${xmlInput.parse(/note/body)}"
          }
        ]
      }
    ]
  },
  "inputs": [
    {
      "name": "xmlInput",
      "type": "byteArray",
      "value": "PG5vdGU+Cjx0bz5Ub3ZlPC90bz4KPGZyb20+SmFuaTwvZnJvbT4KPGhlYWRpbmc+UmVtaW5kZXI8L2hlYWRpbmc+Cjxib2R5PkRvbid0IGZvcmdldCBtZSB0aGlzIHdlZWtlbmQhPC9ib2R5Pgo8L25vdGU+"
    }
  ]
}
```