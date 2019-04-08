package r01f.persistence.index.document;

import java.text.Format.Field;



import lombok.NoArgsConstructor;
import r01f.objectstreamer.annotations.MarshallType;

/**
 * "Document-Stride" fields aka DocValues, used as columns to store field's values to be used when
 * sorting, boosting or simply for getting it's values
 * See:
 * <ul>
 * 		<li>http://www.searchworkings.org/blog/-/blogs/introducing-lucene-index-doc-values</li>
 * 		<li>http://www.slideshare.net/lucenerevolution/willnauer-simon-doc-values-column-stride-fields-in-lucene</li>
 * </ul>
 *
 * NOTE that if using Lucene, a {@link Document} can ONLY store a SINGLE value in each of these fields
 *
 * DETAILS:
 * --------
 * The normal search process is limited to the information available at the inverted index:
 * <pre>
 * 											Lucene Inverted index
 * 		- term                      		-------------------
 * 		- term frequency in the doc 		  term | freq | docId
 * 		- boosting                  		-------------------
 * 			  						  		    A  |  1   | 0,1
 * 			  						  		    B  |  1   | 2,4
 * 			  						  		    C  |  1   | 3
 *
 * </pre>
 * BUT what to do if a custom score / ordering is needed... that info is NOT present at the inverted index
 *
 * In order to achieve quick access to a document's field's values, Lucene have the
 * {@link FieldCache} interface that in the roots is a un-inverted Lucene index
 * that allows quick field's value access, specially to the docId
 * (bear in mind that docValue kind of {@link Field}s can ONLY store a SINGLE TERM)
 * <pre>
 * 			Lucene Inverted index		  Lucene un-inverted index
 * 			-------------------			  --------------------
 * 			term | freq | docId			    docId   |   term
 * 			-------------------           --------------------
 * 			  A  |  1   | 0,1  ---------->    0     |    A
 * 			  B  |  1   | 2,4				  1     |    A
 * 			  C  |  1   | 3					  2		|    B
 * 											  3		|    C
 * 											  4		|    B
 * 	</pre>
 * 	Using this this un-inverted index ({@link FieldCache}) it's very easy to find the docId
 * 	for a term
 * 	The problem with {@link FieldCache} is that it's stored at RAM and it uses a lot of RAM
 * 	if there are a lot of fields or	a lot of {@link Document} / {@link Term}
 *
 * 	The alternative is to MOVE the {@link FieldCache} TO THE INDEX.
 * 	To achieve this target xxxDocValuesField field types has been created
 */
@MarshallType(as="indexDocValueFieldType")
@NoArgsConstructor
public enum IndexDocumentValueFieldType
 implements IndexDocumentFieldType {
	Double,
	Float;
//	Short	// deprecated
//	Int,	// deprecated
//	Long,	// deprecated
}
