package r01f.types;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Numbers;

/**
 * Encapsulates a bitmat into a java int (ints in java are 4 bytes). 
 * For example if we want to encode some security rights:
 * <pre>
 *    Total control ----------------------------------------------
 *    Security admin ----------------------------------------  |
 *    Delete ------------------------------------------------  | |
 *    Read ------------------------------------------------  | | | 
 *    Write ---------------------------------------------  | | | |
 *    Create ------------------------------------------  | | | | |
 *                                                     | | | | | |
 *                               0 0 0 0 0 0 0 0 | 0 0 0 0 0 0 0 0 
 * ie:
 *   X X X 00011010: Can admin security, read or write
 *                   BUT cannot create nor have total control
 * </pre>
 * The normal use of this base class is:
 * <pre class='brush:java'>
 * 		public class MyMask extends BitMap {
 * 			private static transient int ELEMENT_A = 0;
 * 			private static transient int ELEMENT_B = 1;
 * 
 * 			public MyMask setElementA() {
 * 				this.setBit(ELEMENT_A);
 * 				return this;
 * 			}
 * 			public MyMask unsetElementA() {
 * 				this.clearBit(ELEMENT_A);
 * 				return this;
 * 			}
 * 			public boolean isElementASetted() {
 * 				return this.
 * 			}
 * 		}
 * </pre>
 */
@MarshallType(as="bitMap")
@Accessors(prefix="_")
@AllArgsConstructor
public abstract class BitMap 
           implements Debuggable,
           			  Serializable {
	
	private static final long serialVersionUID = -7795853546674157713L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="bitMap",
				   whenXml=@MarshallFieldAsXml(attr=true))
    @Getter @Setter private int _bitMap;      // int containing the data      
    
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////    
     /**
     * Sets the bitMap info from other given bitmap
     * (it computes an logical OR with the two bit maps)
     * @param otherFlags
     */
    public void incorporateBitMapInfo(final BitMap otherFlags) {
        _bitMap |= otherFlags.getBitMap();   // logical OR
    }    
/////////////////////////////////////////////////////////////////////////////////////////
//  INDIVIDUAL BIT SET/CLEAR METHODS 
/////////////////////////////////////////////////////////////////////////////////////////    
    /**
     * Gets a bit value
     * @return the bit value
     */
    protected boolean bitAt(final int bitIndex) {
        return _getBit(_bitMap,
        			   (Numbers.INTEGER_WIDTH*8-1)-bitIndex-1);
    }
    /**
     * Sets a bit value
     * @param bitIndex 
     */
    protected void setBitAt(final int bitIndex) {
        _bitMap = _setBit(_bitMap,
        				  (Numbers.INTEGER_WIDTH*8-1)-bitIndex-1);
    }
    /**
     * Resets a bit value
     * @param bitIndex 
     */
    protected void clearBitAt(final int bitIndex) {
        _bitMap = _clearBit(_bitMap,
        					(Numbers.INTEGER_WIDTH*8-1)-bitIndex-1);
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  GLOBAL SET/CLEAR
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Sets all bit values
     */
    protected void setAll() {
        for (int i=0; i <= (Numbers.INTEGER_WIDTH*8-2); i++) {
        	this.setBitAt(i);
        }    	
    }
    /**
     * Resets all bit values
     */
    protected void clearAll() {
        for (int i=0; i <= (Numbers.INTEGER_WIDTH*8-2); i++) {
        	this.clearBitAt(i);
        }    	
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  DEBUG-INFO
/////////////////////////////////////////////////////////////////////////////////////////    
    @Override
    public String debugInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("Mapa de bits: " + _bitMap + " : " + Integer.toBinaryString(_bitMap) + "\r\n");
        String currBitStr = "";
        for (int i=0; i <= (Numbers.INTEGER_WIDTH*8-2); i++) {
            currBitStr = "  " + i;
            sb.append(currBitStr.substring(currBitStr.length()-2) + "|");
        }
        sb.append("\r\n");
        for (int i=0; i <= (Numbers.INTEGER_WIDTH*8-2); i++) {
            currBitStr = (this.bitAt(i) ? " 1":" 0");
            sb.append(currBitStr.substring(currBitStr.length()-2) + "|");
        }
        sb.append("\r\n");
        return sb.toString();
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i <= (Numbers.INTEGER_WIDTH*8-2); i++) {
            sb.append( (this.bitAt(i)?"1":"0") );
        }
        return sb.toString();        
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  BIT MASKING
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Gets a mask to get an int's bit value
     * @param bitIndex 
     * @return the mask
     */
    private static int _getMask(final int bitIndex) {
        return (1 << bitIndex);
    }
    /**
     * Sets an int's bit and returns the new int
     * @param originalInt
     * @param bitIndex
     * @return the new int
     */
    public static int _setBit(final int originalInt,final int bitIndex) {
        return originalInt | _getMask(bitIndex);
    }
    /**
     * Resets an int's bit and returns the new int
     * @param originalInt
     * @param bitIndex
     * @return the new int
     */
    public static int _clearBit(final int originalInt,final int bitIndex) {
        return originalInt & ~_getMask(bitIndex);
    }
    /**
     * Returns an int's bit value
     * @param integer 
     * @param bitIndex 
     * @return the bit value
     */
    public static boolean _getBit(final int integer,final int bitIndex) {
        return ((integer & _getMask(bitIndex)) != 0);
    }
}
