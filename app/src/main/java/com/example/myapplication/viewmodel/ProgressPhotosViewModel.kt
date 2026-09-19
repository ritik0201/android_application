package com.example.myapplication.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.FitTrackDatabase
import com.example.myapplication.data.entity.ProgressPhoto
import com.example.myapplication.data.repository.FitTrackRepository
import com.example.myapplication.util.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ProgressPhotosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitTrackRepository

    val progressPhotos: StateFlow<List<ProgressPhoto>>

    init {
        val database = FitTrackDatabase.getDatabase(application)
        val userPreferences = UserPreferences(application)
        repository = FitTrackRepository(
            database.userProfileDao(),
            database.exerciseDao(),
            database.workoutPlanDao(),
            database.workoutSessionDao(),
            database.bodyMeasurementDao(),
            database.nutritionDao(),
            database.achievementDao(),
            database.progressPhotoDao(),
            database.exerciseNoteDao(),
            userPreferences
        )

        progressPhotos = repository.progressPhotos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun savePhotoFromUri(uri: Uri, notes: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>().applicationContext
            val photosDir = File(context.filesDir, "progress_photos")
            if (!photosDir.exists()) {
                photosDir.mkdirs()
            }

            val photoFile = File(photosDir, "photo_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(photoFile).use { output ->
                    input.copyTo(output)
                }
            }

            if (photoFile.exists()) {
                repository.saveProgressPhoto(
                    ProgressPhoto(
                        filePath = photoFile.absolutePath,
                        notes = notes
                    )
                )
            }
        }
    }

    fun deletePhoto(photo: ProgressPhoto) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(photo.filePath)
            if (file.exists()) {
                file.delete()
            }
            repository.deleteProgressPhoto(photo)
        }
    }
}
