# ProJson

ProJson is a small JSON library for Kotlin. It has two parts: an in-memory model that
represents a JSON document as a tree of objects, and a serializer that turns ordinary
Kotlin objects into that tree using reflection. It only depends on the Java standard
library, with JUnit 5 used for the tests.

## What it does

You can work with JSON in two ways. The first is to build the tree by hand using the
model classes (`JsonObject`, `JsonArray`, `JsonPrimitive`). The second is to hand any
Kotlin object to `ProJson().toJson(...)` and let it walk the object's fields with
reflection and produce the tree for you. Either way, calling `toJsonString()` on the
result gives you the JSON text.

The serializer also understands a few annotations so you can control the output, and it
can replace repeated objects with references instead of writing them out more than once.

## Architecture

![ProJson package diagram](projson-diagram.svg)

The model is built around the `JsonElement` interface. `JsonPrimitive`, `JsonObject`,
`JsonArray` and `JsonReference` all implement it, so any node can be printed with
`toJsonString()`. `ProJson` is the entry point that produces those elements from Kotlin
objects, and the annotations let you adjust how a class is serialized.

## Quick start

```kotlin
val json = ProJson().toJson(myObject)
println(json.toJsonString())
```

You can also build a document by hand without any reflection:

```kotlin
val obj = JsonObject()
obj.setProperty("name", "Alice")
obj.setProperty("age", 30)
println(obj.toJsonString())   // {"name": "Alice", "age": 30}
```

## A worked example

Take a couple of classes like the ones from the project brief:

```kotlin
data class Date(val day: Int, val month: Int, val year: Int)
class Task(val description: String, val deadline: Date?, val dependencies: List<Task>)
```

Serializing a task:

```kotlin
val t1 = Task("T1", Date(1, 1, 2026), emptyList())
println(ProJson().toJson(t1).toJsonString())
```

prints:

```json
{"$id": "341227df-04f0-4252-9375-e00256ed3e27", "$type": "Task", "description": "T1", "deadline": {"$type": "Date", "day": 1, "month": 1, "year": 2026}, "dependencies": []}
```

The `$id` is a UUID created the first time an object is written, so its value is different
on every run. `Task` is a regular class, which is why it gets an `$id`, while `Date` is a
data class and only gets a `$type`. That difference is explained below.

## Features and API

The model classes cover the usual JSON shapes:

- `JsonPrimitive` holds a single value: `null`, a `String`, a `Number` or a `Boolean`.
  Strings are escaped when printed.
- `JsonObject` keeps its keys in insertion order. You can add or overwrite a key with
  `setProperty`, read one with `getProperty`, remove one with `deleteProperty`, and walk
  every entry with `visit`.
- `JsonArray` holds a list of elements and lets you `add`, `remove`, `get`, ask for its
  `size`, and `visit` each item.
- `JsonReference` is how a repeated object is written, as `{"$ref": "<id>"}`.

When `ProJson` serializes a regular class it writes a `$type` field with the class name
and a `$id` field with a generated UUID. Data classes are treated as plain values, so
they get a `$type` but no `$id`. ProJson tells the two apart by checking for the `copy`
method that the Kotlin compiler only generates for data classes.

Three annotations change how a class is written:

- `@JsonIgnore` on a field leaves that field out of the output.
- `@JsonProperty("name")` on a field uses a different key in the JSON.
- `@JsonString` on a class serializes the whole object to a single string, using a
  `JsonStringSerializer` you provide.

A fourth annotation, `@Reference`, deals with shared objects. When a field is marked with
it and the object it points to has already been written once, ProJson writes a `$ref` to
that object's `$id` instead of repeating it. This also stops circular references from
looping forever.

## Spec coverage

The pieces of the brief map onto the source files like this:

- The in-memory model is in `JsonElement.kt`, `JsonObject.kt`, `JsonArray.kt` and
  `JsonReference.kt`.
- Reflection-based serialization and the data-class handling are in `ProJson.kt`.
- The annotations and the `JsonStringSerializer` interface are in `Annotations.kt`.
- References (`$id`, `$ref` and `@Reference`) are handled in `ProJson.kt` together with
  `JsonReference.kt`.

Each feature has matching tests under `src/test/kotlin/projson`.

## Building and testing

The project uses Gradle. To compile and run the tests:

```
./gradlew test
```

The tests are written with JUnit 5 and cover the model classes, the serializer, the
annotations and the reference handling.
