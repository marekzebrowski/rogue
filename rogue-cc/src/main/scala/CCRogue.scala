import com.mongodb.DBObject


trait CCMeta[R] {
    def collectionName: String
    def fromDBObject(dbo: DBObject): R
}