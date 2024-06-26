package dev.banger.hootkey.presentation.entity

import android.os.Parcel
import android.os.Parcelable

data class UiTemplateShort(
    val id: String,
    val name: String,
    val isCustom: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeByte(if (isCustom) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UiTemplateShort> {
        override fun createFromParcel(parcel: Parcel): UiTemplateShort {
            return UiTemplateShort(parcel)
        }

        override fun newArray(size: Int): Array<UiTemplateShort?> {
            return arrayOfNulls(size)
        }
    }
}
