package dev.banger.hootkey.presentation.entity

import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

data class UiTemplateField(
    val uuid: UUID = UUID.randomUUID(),
    val name: String = "",
    val type: UiFieldType = UiFieldType.LOGIN,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        UUID.fromString(parcel.readString()),
        parcel.readString() ?: "",
        UiFieldType.valueOf(parcel.readString() ?: "")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid.toString())
        parcel.writeString(name)
        parcel.writeString(type.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UiTemplateField> {
        override fun createFromParcel(parcel: Parcel): UiTemplateField {
            return UiTemplateField(parcel)
        }

        override fun newArray(size: Int): Array<UiTemplateField?> {
            return arrayOfNulls(size)
        }
    }

}
