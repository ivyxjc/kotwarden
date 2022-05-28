package com.ivyxjc.kotless

import io.ktor.events.EventDefinition
import io.ktor.server.application.*

/**
 * Event that will be emitted during warming of lambda.
 */
val LambdaWarming = EventDefinition<Application>()
