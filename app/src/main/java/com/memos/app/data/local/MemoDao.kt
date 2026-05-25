package com.memos.app.data.local

import androidx.room.*
import com.memos.app.data.local.entities.MemoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {

    @Query("""
        SELECT * FROM memos
        WHERE rowStatus = 'NORMAL'
        ORDER BY pinned DESC, createTime DESC
    """)
    fun getAllMemos(): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memos WHERE id = :id")
    suspend fun getMemoById(id: Int): MemoEntity?

    @Query("SELECT * FROM memos WHERE name = :name")
    suspend fun getMemoByName(name: String): MemoEntity?

    @Query("""
        SELECT * FROM memos
        WHERE rowStatus = 'NORMAL'
          AND (content LIKE '%' || :q || '%'
               OR tagsJson LIKE '%' || :q || '%')
        ORDER BY createTime DESC
    """)
    fun searchMemos(q: String): Flow<List<MemoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemos(memos: List<MemoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: MemoEntity)

    @Query("DELETE FROM memos WHERE id = :id")
    suspend fun deleteMemoById(id: Int)

    @Query("DELETE FROM memos")
    suspend fun clearAll()
}
