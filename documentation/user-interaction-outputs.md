# Reusing user interaction values

Each field configured in `interactionInputs` becomes an output with the same ID.
Text and single selections produce `StringProperty`, booleans produce
`BooleanProperty`, and files produce `ByteArrayProperty` with filename and MIME
metadata. The existing static `getOutputsDefinition()` stays empty because field
names are specific to each configured interaction.

Configure mappings on the interaction step, for example:

```json
"outputMappings": {
  "patientId": "selectedPatientId",
  "document": "documentToValidate"
}
```

A later simulation step can use `${selectedPatientId}` in a text input. A later
validation step can use `${documentToValidate}` in its byte-array input. These
are context references resolved during execution, before recording.

Field IDs must be unique and nonblank. Responses must use configured IDs and
matching types; unknown fields, duplicate responses, references submitted as
values, and absent required values are rejected. Boolean `false` is a valid
answer. Unanswered optional fields produce no output. Mapping an absent output
retains the engine's existing explicit missing-output failure behavior.

Report inputs retain all submitted entries, including null and blank optional values.
Only outputs omit unanswered values. Definition validation reuses the existing
SupportedInput validators; response-specific checks live in UserInteractionResponseValidator. Outputs are separate
property copies. At recording time, uploaded files become attachments and both
report views point to the same attachment, avoiding duplicate embedded payloads.
Scalars remain inline.

Recording links a consumer to the step that produced its file by alias. To keep
that unambiguous, test validation rejects aliases published more than once or
matching declared test inputs. Run validation also rejects collisions with actual
inputs, including suite inputs and per-reference inputs not declared by the test.
Aliases may repeat in separate tests, which have independent contexts.

ExecutionFinished already carries the original TestRun or TestSuiteRun. At
completion, RecordingMaestro supplies its test definitions alongside the report.
The recorder matches non-null test IDs and unique step names, giving the original
output mappings directly to StepOutputPlan. Unknown step names receive no aliases;
there is no positional fallback. Recording plans follow the definition's execution
order so producer attachments resolve before consumer reports are persisted, even
when the supplied report list has been reordered. Missing steps do not shift mappings. Reports, DTOs, sessions and completion
events carry no additional mapping fields or provenance context.

Only mapped outputs announce attachment IDs to consumer callbacks. Unmapped
outputs cannot collide with test input names. Standalone recording without the
original definitions can persist files, but cannot announce their output aliases.
Historical nested subreports are recorded without assuming that the parent test
definitions describe their execution.

On retry, interaction file references already containing /attachments/<id> are
scheduled as existing attachments and published to consumers without reuploading
bytes.

Verification lives in `UserInteractionStepExecutorTest`,
`UserInteractionStepOutputDissectorTest`, and
`MaestroEngineTest.interactionResponseIsMappedIntoNextStep`.

`InteractionFileRecordingTest` exercises real interaction and validation dissectors
through the Datahouse recorder with two producers, an unmapped output, and a
failed root-write retry.
