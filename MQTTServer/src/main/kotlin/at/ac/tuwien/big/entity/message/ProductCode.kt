package at.ac.tuwien.big.entity.message

/**
 * Information included in workpiece tags, encoded in QR codes
 */
data class ProductCode(var id: String, var batchId: String, var color: String, var base64: String?)