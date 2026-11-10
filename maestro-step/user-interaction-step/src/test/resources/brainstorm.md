# Tasks

- Timeout handling improvement
  - step without timeout -> full test session timeout -> if no test session timeout is provided -> 3min
  - Timeout defined in steps is priority
      - Timeout defined in startTestRun message is then priority
      - Default Timeout is used if nothing is provided
- Migrate SQL to mongoDB
- Fix REST controller to align with websocket controller
- reconnect message to restore websocket connection between test exec and maestro
- Add Optional steps -> an optional step cannot have outputmappings in test model validation


- Add userId and dateTime in Property OK
- Create LINK property type OK
- Create Evidence concept OK
- Create PUT_EVIDENCE message type OK
- Add gradingMethod in Test model OK
- Handle stop execution for gradingMethod "monitored" at the end of the execution, 
waiting for the user to set the status to completed or if there is at least one step
that accept unsolicited evidence OK
- Add a new evaluation step in maestro that is always in a test and maestro has to wait this step is over to terminate the test OK

# Brainstorming asynchronous step

Add 2 new websocket messages for "pausing" step and "StepResumed" step
- New Message StepPaused
- New status for a step : PAUSED
- Do we start a new timer with maximum duration ? 24h ? 48h ?
- New Message StepResumed

Add 1 new websocket message for "upload input" for test execution
- New Message InputUploaded -> triggers test execution to store the input

# Brainstorming tests migration TM > TMR

## Step Transaction

### Peer to peer / Simulator
View of a transaction to be performed
Add evidences for this transaction
Proxy integration with message detection / suggestion

## Evidences

How to upload any evidences
How to validate any evidences

## Test without any steps

Some test has no steps -> how to migrate it ?
User interaction with only a description and the placeholder for evidences


# Brainstorming User interaction

## Simple frontend workaround

- When the execution reach a user interaction step that contains input, we ask the user to upload his file
through the display message, explaining him to search the right input in the test configuration part.
- Source of errors if the user put the file in the wrong input
- Does not respect requirements
- Last chance solution


## Change Maestro test model

- Add a List<SupportedInput> in Step and StepRun model
- This has big impact on the frontend and for test scripts as JSON representation.
- Maybe we can define a subtype of stepRun specifically for user interaction step.


## Add a new type of Property

- New type of property for List<SupportedInput> value type
  - Not resolved version SUPPORTED_INPUT PropertyType = List<SupportedInput>
- Add a SUPPORTED_INPUT property as user interaction definition named "interactionInputs"
- Validate property name more strictly : prohibit ":" character


## Trick frontend to display correctly user interaction step

- Add a defensive code in frontend that solve input references if we are in a user interaction step.
- Instead of displaying those inputs in the Test level, we take them out from the List<SupportedInput> of the test
and put them in the user interaction step level. 
- When user interface send the test run command, the property is already resolved with its actual content.
- This should have low impact in backend models (TMR / maestro)

PS : not sure if this solution is really the best, as the principle of the user interaction step
is to be sent back from test execution engine to the frontend with upcoming supported inputs.
If supported input are already present in the test script before any test execution, then the user interaction step
does not really have a sense, it becomes just a step were we ask the user to upload a file and that's it.

PPS : except if we are in the case were the user interaction step is purely for uploading test evidence, then
it can be defined in advance by the test script ?

PPPS : maybe I misunderstand the real goal of defining a user interaction step with input in it