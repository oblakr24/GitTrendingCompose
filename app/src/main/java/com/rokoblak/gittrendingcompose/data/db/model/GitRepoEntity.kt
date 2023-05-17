package com.rokoblak.gittrendingcompose.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GitRepoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val desc: String?,
    val authorImgUrl: String?,
    val authorName: String,
    val lang: String?,
    val stars: Long,
    val pageIdx: Int,
    val orderIdx: Int,
    val timestampMs: Long,
)