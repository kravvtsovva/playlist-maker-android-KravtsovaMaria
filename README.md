# Playlist Maker

Учебный проект по созданию мобильного приложения для поиска и управления плейлистами.

## Сборка
- Android Studio Otter 2 Feature Drop | 2025.2.2+
- Min SDK 24, Target SDK 36
- Compose BOM 2025.12.00
- Kotlin 2.2.21
- Navigation Compose 2.9.6


## Зависимости
- Jetpack Compose (BOM 2025.12.00) для UI
- Compose Navigation 2.9.6 для навигации
- Room 2.8.4 для локальной базы данных
- DataStore 1.2.0 для хранения истории поиска
- Retrofit 3.0.0 для работы с API (эмулятор сервера)
- Coil 2.7.0 для загрузки изображений
- Gson 2.13.2 для парсинга JSON

## Сборка
- git clone https://github.com/DronZed23/playlist-maker-android-VinogradovDmitry.git

- Откройте Android Studio версии 2025.2.2 или новее
- Выберите "Open" и укажите папку проекта, которая была создана после клонирования

- Синхронизация Gradle
- Дождитесь завершения синхронизации зависимостей
- При необходимости обновите Gradle и зависимости
- Выберите сборку в меню Build → Make Project (Ctrl+F9)
- Убедитесь, что сборка завершена без ошибок