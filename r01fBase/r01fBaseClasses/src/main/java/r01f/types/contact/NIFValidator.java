package r01f.types.contact;

import r01f.validation.ObjectValidationResult;
import r01f.validation.ObjectValidationResultBuilder;
import r01f.validation.Validates;

public class NIFValidator 
  implements Validates<NIFPersonID> {
/////////////////////////////////////////////////////////////////////////////////////////
//  DNI VALIDATOR (see http://www.agenciatributaria.es/static_files/AEAT_Desarrolladores/EEDD/DescargaModulos/2012/Ficheros/ValNif08201.zip)
/////////////////////////////////////////////////////////////////////////////////////////
    private static final int NIF_ERROR = -1;
    private static final int NIF_ERROR_TAMANO = -2;
    private static final int NIF_ERROR_CARACTERES = -4;
    private static final int NIF_ERROR_3LETRAS = -5;
    private static final int CIF_ERROR_DC = -10;
    private static final int NIF_ERROR_DC = -11;
    private static final int NIF_ERROR_NUM = -12;
    private static final int NIF_ERROR_DOSNUM = -13;
    private static final int DNI_ERROR_MAX = -20;
    private static final int DNI_ERROR_VALOR = -21;
    private static final int DNI_OK = 0;
    private static final int NIF_OK = 1;
    
    private static final int NIF_NORESIDENTES = 2;
    private static final int NIF_MENORES14ANOS = 3;
    private static final int NIF_EXTRANJEROS = 4;
    
    private static final int CIF_OK = 20;
    private static final int CIF_EXTRANJERO_OK = 21;
    private static final int CIF_ORGANIZACION_OK = 22;
    private static final int CIF_NORESIDENTES_OK = 23;
	
	
    private static final char NUMBERS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char LETTERS[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
        								  'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 
        								  'W', 'X', 'Y', 'Z'};
    private static final char NIF_LETTERS[] = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 
        									   'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L',
        									   'C', 'K', 'E'};
    private static final char CIF2_LETTERS[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
    private static final char CIF_LETTERS[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'U','V'};
    public static final char CIFORG_AND_EXTR_LETTERS[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'P', 'Q', 'S','N', 'W', 'R'};
    private static final char REGATRIBRENTAS_LETTERS[] = {'E', 'G', 'H', 'J', 'U', 'V'};
    private static final char CIFEXT_LETTERS[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'N', 'W'};
    private static final char NIFEXT_LETTERS[] = {'X', 'Y', 'Z'};
    

/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public ObjectValidationResult<NIFPersonID> validate(final NIFPersonID nif) {
		boolean nifValid = (_validateNif(nif) >= 0);
		return nifValid ? ObjectValidationResultBuilder.on(nif)
													   .isValid()
						: ObjectValidationResultBuilder.on(nif)
													   .isNotValidBecause("Not valid nif={}",nif);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	private int _validateNif(final NIFPersonID nif) {
    	if (nif == null) return NIF_ERROR;
        int k1 = 0;
        String theNif = nif.getId().trim();
        if (theNif.length() != 9) {
            return NIF_ERROR_TAMANO;
        }
        char c = '\0';
        char c1 = '\0';
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        char ac[] = theNif.toCharArray();
        for (int i = 0; i < 9; i++) {
            char c2 = ac[i];
            int i3 = Character.getType(c2);
            if (i3 == 1 && i2 == 0) {
                i2++;
                c = c2;
                j2 = i;
                continue;
            }
            if (i3 == 1 && i2 == 1) {
                i2++;
                c1 = c2;
                k2 = i;
                continue;
            }
            if (i3 == 1 && i2 == 2) {
                return NIF_ERROR_3LETRAS;
            }
            if (i3 != 9) {
                return NIF_ERROR_CARACTERES;
            }
        }

        if (i2 == 0) {
            if (ac[0] != '0') {
                return DNI_ERROR_MAX;
            }
            String s3 = theNif.substring(1);
            return !s3.equals("11111111") && !s3.equals("22222222") && !s3.equals("33333333") && !s3.equals("44444444") && !s3.equals("55555555") 
                && !s3.equals("66666666") && !s3.equals("77777777") && !s3.equals("88888888") && !s3.equals("99999999") && !s3.equals("00000000") ? DNI_OK : DNI_ERROR_VALOR;
        }
        if (i2 == 1 && _caracEnCad(CIF_LETTERS, ac[j2]) && j2 == 0 && Character.isDigit(ac[8])) {
            int j;
            for (j = 1; j < 8; j++) {
                if (j == 2 || j == 4 || j == 6) {
                    k1 += ac[j] - 48;
                    continue;
                }
                int i1 = (ac[j] - 48) * 2;
                if (i1 > 9) {
                    i1 -= 9;
                }
                k1 += i1;
            }

            k1 = 10 - k1 % 10;
            if (k1 == 10) {
                k1 = 0;
            }
            if (k1 == ac[j] - 48) {
                return !_caracEnCad(REGATRIBRENTAS_LETTERS, ac[j2]) ? CIF_OK : CIF_NORESIDENTES_OK;
            } 
            return CIF_ERROR_DC;
        }
        if (i2 == 2 && _caracEnCad(CIFORG_AND_EXTR_LETTERS, ac[j2]) && j2 == 0 && k2 == 8 && _caracEnCad(CIF2_LETTERS, ac[k2])) {
            for (int k = 1; k < 8; k++) {
                if (k == 2 || k == 4 || k == 6) {
                    k1 += ac[k] - 48;
                    continue;
                }
                int j1 = (ac[k] - 48) * 2;
                if (j1 > 9) {
                    j1 -= 9;
                }
                k1 += j1;
            }

            k1 = 10 - k1 % 10;
            if (CIF2_LETTERS[k1 - 1] == ac[k2]) {
                return !_caracEnCad(CIFEXT_LETTERS, ac[j2]) ? CIF_ORGANIZACION_OK : CIF_EXTRANJERO_OK;
            } 
            return CIF_ERROR_DC;
        }
        if (i2 == 1 && _caracEnCad(LETTERS, ac[8]) && _caracEnCad(NIF_LETTERS, ac[j2]) && j2 == 8) {
            StringBuffer stringbuffer = new StringBuffer(theNif.substring(0,j2));
            long l2 = Long.parseLong(stringbuffer.toString());
            long l6 = l2 % 23L;
            if (l6 + 1L > 23L) {
                return NIF_ERROR_NUM;
            }
            if (c == NIF_LETTERS[(int)l6]) {
                return !theNif.equals("00000001R") && !theNif.equals("00000000T") && !theNif.equals("99999999R") ? NIF_OK : NIF_ERROR;
            } 
            return NIF_ERROR_DC;
        }
        if (i2 == 2 && (ac[0] == 'K' || ac[0] == 'L' || ac[0] == 'M') && _caracEnCad(NIF_LETTERS, ac[k2]) && k2 == 8) {
            String s1 = theNif.substring(1,3);
            if (!_caracEnCad(NUMBERS,s1.charAt(0)) || !_caracEnCad(NUMBERS,s1.charAt(1))) {
                return NIF_ERROR_DOSNUM;
            }
            int l = Integer.parseInt(s1);
            if (l < 1 || l > 56) {
                return NIF_ERROR;
            }
            s1 = theNif.substring(1,k2);
            long l3 = Long.parseLong(s1);
            long l7 = l3 % 23L;
            l7++;
            if (l7 > 23L) {
                return NIF_ERROR_NUM;
            }
            return c1 != NIF_LETTERS[(int)(l7 - 1L)] ? NIF_ERROR_DC : NIF_NORESIDENTES;
        }
        if (theNif.equals("X0000000T")) {
            return NIF_ERROR;
        }
        if (i2 == 2 && _caracEnCad(NIFEXT_LETTERS, ac[0]) && _caracEnCad(NIF_LETTERS, ac[k2]) && k2 == 8) {
            String s2 = theNif.substring(1,k2);
            long l4 = Long.parseLong(s2);
            if (ac[0] == 'Y') {
                l4 += 0x989680L;
            } else
            if (ac[0] == 'Z') {
                l4 += 0x1312d00L;
            }
            long l8 = l4 % 23L;
            l8++;
            if (l8 > 23L) {
                return NIF_ERROR_NUM;
            }
            return c1 != NIF_LETTERS[(int)(l8 - 1L)] ? NIF_ERROR_DC : NIF_EXTRANJEROS;
        } 
        return NIF_ERROR;
	}

    private static boolean _caracEnCad(final char ac[],final char c) {
        boolean flag = false;
        int i = ac.length;
        int j = 0;
        do {
            if (j >= i) {
                break;
            }
            if (ac[j] == c) {
                flag = true;
                break;
            }
            j++;
        } while (true);
        return flag;
    }
}
 
