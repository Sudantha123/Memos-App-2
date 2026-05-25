package com.memos.app.data.local

import android.content.Context
import androidx.room.*
import com.memos.app.data.local.entities.MemoEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(
    entities = [MemoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMemoDatabase(
        @ApplicationContext context: Context
    ): MemoDatabase = Room.databaseBuilder(
        context,
        MemoDatabase::class.java,
        "memos.db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideMemoDao(db: MemoDatabase): MemoDao = db.memoDao()
}
