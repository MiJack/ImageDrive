package cn.mijack.imagedrive.entity

/**
 * @author admin
 * @date 2017/8/26
 */
class Folder : Media {
    var count: Int
        get() = count
        set(value) {
            count = value
        }
    var data: List<Image>
        get() = data
        set(value) {
            data = value
        }

    constructor(path: String) : super(path)

}