package be.aidenstorme.todoapplication.di

import javax.inject.Qualifier

/** Qualifier voor de CoroutineScope die zolang leeft als de app. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
