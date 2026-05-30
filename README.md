# MyPersonality Client

Android-клиент сервиса подбора персонала.

## Стек

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Hilt
- Retrofit
- DataStore

## Возможности

- регистрация и вход
- роли `соискатель / работодатель`
- поиск вакансий
- история поиска
- избранное
- отклики
- вакансии работодателя
- создание, редактирование и удаление вакансии
- темная тема

## Запуск

1. Убедитесь, что backend запущен на `http://10.0.2.2:8080/`.
2. Выполните:

```bash
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' GRADLE_USER_HOME=/tmp/gradle-home ./gradlew :app:assembleDebug
```

## Проверка

```bash
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' GRADLE_USER_HOME=/tmp/gradle-home ./gradlew :app:testDebugUnitTest :app:assembleDebugAndroidTest
```
