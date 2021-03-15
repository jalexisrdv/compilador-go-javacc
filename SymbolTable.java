import java.util.HashMap;

/**
 *
 * @author jardv
 */
class SymbolTable {
    
    private static HashMap<String, Integer> hm;
    private static String errorsSemantics = "";
    private static String msg;

    public SymbolTable() {
        hm = new HashMap<>();
    }
    
    public static boolean containsId(String id) {
        return hm.containsKey(id);
    }
    
    public static String insertSymbol(Token token, int dataType) {
        if(!containsId(token.image)) {
            hm.put(token.image, dataType);
            return "";
        }else {
            msg = "Error la variable " + token.image + 
                    " ya se ha declarado" + " en la linea " + token.beginLine + "\n";
            return msg;
        }
    }
    
    //Devuelve el valor de la constante del token, del tipo de dato asociado
    public static Integer getSymbolDataType(String id) {
        return hm.get(id);
    }
    
    /*Metodos para validar asignaciones en variables
    el parametro id, siempre es un token consumido de tipo
    identificador, por ese motivo no hace falta comprobar si
    el token es de tipo id
    */
    public static String checkAssignment(Token id, Token value, Token operator) {
        
        int idDataType = 0;
        int valueDataType = 0;
        
        //Si la variable declarada existe, dame el tipo de dato
        if(containsId(id.image)) {
            idDataType = getSymbolDataType(id.image);
        }else {
            msg = "Error la variable " + id.image + " aun no se ha declarado "
                    + " en la linea " + id.beginLine + "\n";
            return msg;
        }

        /*Comprobando si el id es de tipo boolean, este no puede utilizar ningun operador*/
        if(idDataType == GoConstants.dt_bool) {
            if(operator != null) {
                msg = "Operador invalido:" +  " operador "+ operator.image + " no definido en tipo de dato "
                    + GoConstants.tokenImage[idDataType] + "\n";
                return msg;
            }
        }

        /*Comprobando si el id es de tipo string, entonces el unico operador que se puede aplicar es:
            + (addition)
        */
        if(idDataType == GoConstants.dt_string) {
            if(operator != null) {
                if(operator.kind != GoConstants.addition) {
                    msg = "Operador invalido:" +  " operador "+ operator.image + " no definido en tipo de dato "
                    + GoConstants.tokenImage[idDataType] + "\n";
                    return msg;
                }
            }
        }
        
        /*verificar si el dato asignado al id, es una literal o una variable (id)*/
        int v = value.kind;
        switch(v) {
            case GoConstants.id:
                /*Si el valor asignado a id es tambien un identificador, 
                este identificador tendra un tipo de dato asignado*/
                //verificar si la variable esta declarada
                if(containsId(value.image)) {
                    int dt = getSymbolDataType(value.image);
                    switch(dt) {
                        case GoConstants.dt_int:
                        case GoConstants.dt_float64:
                        case GoConstants.dt_string:
                        case GoConstants.dt_bool:
                            valueDataType = dt;
                        break;
                    }
                }else {
                    msg = "Error la variable " + value.image + " aun no se ha declarado " 
                            + " en la linea " + id.beginLine + "\n";
                    return msg;
                }
            break;
            case GoConstants.integer_literal:
            case GoConstants.floating_literal:
            case GoConstants.string_literal:
            case GoConstants.boolean_literal:
                valueDataType = value.kind;
            break;
            default:
                //valor por default = 0
                valueDataType = GoConstants.DEFAULT;
        }
        
        /*Verificando que el valor asignado al id, sea compatible con tipo de dato del id*/
        switch(idDataType) {
            case GoConstants.dt_int:
                //Tipo de dato int y float, son compatibles.
                switch(valueDataType) {
                    //Si entra a cualquiera de los casos, quiere decir que la literal es compatible con el tipo de dato
                    case GoConstants.dt_int:
                    case GoConstants.integer_literal:
                    break;
                    default:
                        msg = "Error no se puede convertir " + value.image 
                                + " a " + GoConstants.tokenImage[idDataType]
                                + " en la linea " + id.beginLine + "\n";
                        return msg;
                        
                }
            break;
            case GoConstants.dt_float64:
                //Tipo de dato int y float, son compatibles.
                switch(valueDataType) {
                    //Si entra a cualquiera de los casos, quiere decir que la literal es compatible con el tipo de dato
                    case GoConstants.dt_float64:
                    case GoConstants.floating_literal:
                    case GoConstants.dt_int:
                    case GoConstants.integer_literal:
                    break;
                    default:
                        msg = "Error no se puede convertir " + value.image 
                                + " a " + GoConstants.tokenImage[idDataType]
                                + " en la linea " + id.beginLine + "\n";
                        return msg;
                        
                }
            break;
            case GoConstants.dt_string:
                switch(valueDataType) {
                    case GoConstants.dt_string:
                    case GoConstants.string_literal:
                    break;
                    default:
                        msg = "Error no se puede convertir " + value.image 
                                + " a " + GoConstants.tokenImage[idDataType]
                                + " en la linea " + id.beginLine + "\n";
                        return msg;
                }
            break;
            case GoConstants.dt_bool:
                switch(valueDataType) {
                    case GoConstants.dt_bool:
                    case GoConstants.boolean_literal:
                    break;
                    default:
                        msg = "Error no se puede convertir " + value.image 
                                + " a " + GoConstants.tokenImage[idDataType]
                                + " en la linea " + id.beginLine + "\n";
                        return msg;
                }
                
        }
        
        /*si los valores son compatibles con el tipo de dato id, no se retorna algun mensaje de error, se retorna = ""*/

        return "";
    }
    
    /*Metodo para validar valores dentro de una condicional, se puede mejorar mucho (este codigo es muy simple al validar)*/
    public static String checkValues(Token value1, Token value2) {
        
        int value1DataType = value1.kind;
        int value2DataType = value2.kind;
        
        if(value1.kind == GoConstants.id) {
            //Si la variable declarada existe, almacena el tipo de dato
            if(containsId(value1.image)) {
                value1DataType = getSymbolDataType(value1.image);
            }else {
                msg = "Error la variable " + value1.image + " aun no se ha declarado "
                        + " en la linea " + value1.beginLine + "\n";
                return msg;
            }
        }
        
        if(value2.kind == GoConstants.id) {
            //Si la variable declarada existe, almacena el tipo de dato
            if(containsId(value2.image) && value2.kind == GoConstants.id) {
                value2DataType = getSymbolDataType(value2.image);
            }else {
                msg = "Error la variable " + value2.image + " aun no se ha declarado "
                        + " en la linea " + value2.beginLine + "\n";
                return msg;
            }
        }
        
        /*Verificando que el valor asignado al id, sea compatible con tipo de dato del id*/
        switch(value1DataType) {
            case GoConstants.dt_int:
            case GoConstants.integer_literal:
                switch(value2DataType) {
                    //Si entra a cualquiera de los casos, quiere decir que la literal es compatible con el tipo de dato
                    case GoConstants.dt_int:
                    case GoConstants.integer_literal:
                    break;
                    default:
                        msg = "Operacion invalida: tipos no coincidentes(" 
                                + GoConstants.tokenImage[getLiteralDataType(value1DataType)] 
                                + " e " + GoConstants.tokenImage[getLiteralDataType(value2DataType)]
                                + ") en la linea " + value1.beginLine + "\n";
                        return msg;
                        
                }
            break;
            //Estos casos son los tipos de datos que acepta la variable a la caul se realizaran asignaciones
            case GoConstants.dt_float64:
            case GoConstants.floating_literal:
                switch(value2DataType) {
                    //Si entra a cualquiera de los casos, quiere decir que la literal es compatible con el tipo de dato
                    case GoConstants.dt_float64:
                    case GoConstants.floating_literal:
                    case GoConstants.dt_int:
                    case GoConstants.integer_literal:
                    break;
                    default:
                        msg = "Operacion invalida: tipos no coincidentes(" 
                                + GoConstants.tokenImage[getLiteralDataType(value1DataType)] 
                                + " e " + GoConstants.tokenImage[getLiteralDataType(value2DataType)]
                                + ") en la linea " + value1.beginLine + "\n";
                        return msg;
                        
                }
            break;
            case GoConstants.dt_string:
            case GoConstants.string_literal:
                switch(value2DataType) {
                    case GoConstants.dt_string:
                    case GoConstants.string_literal:
                    break;
                    default:
                        msg = "Operacion invalida: tipos no coincidentes(" 
                                + GoConstants.tokenImage[getLiteralDataType(value1DataType)] 
                                + " e " + GoConstants.tokenImage[getLiteralDataType(value2DataType)]
                                + ") en la linea " + value1.beginLine + "\n";
                        return msg;
                }
            break;
            case GoConstants.dt_bool:
            case GoConstants.boolean_literal:
                switch(value1DataType) {
                    case GoConstants.dt_bool:
                    case GoConstants.boolean_literal:
                    break;
                    default:
                        msg = "Operacion invalida: tipos no coincidentes(" 
                                + GoConstants.tokenImage[getLiteralDataType(value1DataType)] 
                                + " e " + GoConstants.tokenImage[getLiteralDataType(value2DataType)]
                                + ") en la linea " + value1.beginLine + "\n";
                        return msg;
                }
                
        }
        
        /*si los valores son compatibles con el tipo de dato id, no se retorna algun mensaje de error, se retorna = "" */

        return "";
    }
    
    private static int getLiteralDataType(int value) {
        switch(value) {
            case GoConstants.integer_literal:
                return GoConstants.dt_int;
            case GoConstants.floating_literal:
                return GoConstants.dt_float64;
            case GoConstants.boolean_literal:
                return GoConstants.dt_bool;
            case GoConstants.string_literal:
                return GoConstants.dt_string;
        }
        /*Si no entra a algun caso anterior, entonces eso quiere decir que el value
        es un id y tiene el tipo de dato asociado*/
        return value;
    }
    
    private static void saveErrorsSemantics(String msg) {
        errorsSemantics += msg + "\n";
    }
    
    public static String getErrorsSemantics() {
        return errorsSemantics;
    }
    
}
