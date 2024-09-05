OpenTelemetry Java Example with Two Interacting Services

This repository contains a Java application instrumented with OpenTelemetry to demonstrate tracing across two services, with methods in one class invoking methods in the other. Each class represents a distinct service with its own OpenTelemetry tracer, and each method creates spans with unique attributes that are exported to Grafana Tempo or another OpenTelemetry-compatible tracing system.

Trace Flow

	1.	OpenTelemetryServiceA.firstMethod():
	•	Starts a span named "ServiceA.firstMethod".
	•	Invokes OpenTelemetryServiceB.firstMethod().
	2.	OpenTelemetryServiceB.firstMethod():
	•	Starts a child span named "ServiceB.firstMethod", as it is called from ServiceA.
	•	Invokes OpenTelemetryServiceA.secondMethod().
	3.	OpenTelemetryServiceA.secondMethod():
	•	Starts another child span named "ServiceA.secondMethod", which is invoked by ServiceB.
	•	Invokes OpenTelemetryServiceB.secondMethod().
	4.	OpenTelemetryServiceB.secondMethod():
	•	Starts a span named "ServiceB.secondMethod", which completes the trace.

Trace Hierarchy Example

This is how the trace hierarchy will look:

	•	Service A: firstMethod (Parent Span)
	•	Service B: firstMethod (Child Span of A’s firstMethod)
	•	Service A: secondMethod (Child Span of B’s firstMethod)
	•	Service B: secondMethod (Child Span of A’s secondMethod)

Each span contains attributes identifying the service (A or B), the method, and the operation being performed.
