package demo;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class Message implements java.io.Serializable
{

   static final long serialVersionUID = 1L;

   @org.kie.api.definition.type.Label(value = "Id user")
   private java.lang.Integer idUser;
   @org.kie.api.definition.type.Label(value = "Message code")
   private java.lang.Integer code;

   public Message()
   {
   }

   public java.lang.Integer getIdUser()
   {
      return this.idUser;
   }

   public void setIdUser(java.lang.Integer idUser)
   {
      this.idUser = idUser;
   }

   public java.lang.Integer getCode()
   {
      return this.code;
   }

   public void setCode(java.lang.Integer code)
   {
      this.code = code;
   }

   public Message(java.lang.Integer idUser, java.lang.Integer code)
   {
      this.idUser = idUser;
      this.code = code;
   }

   @Override
   public String toString() {
      return "Message{" +
              "idUser=" + idUser +
              ", code=" + code +
              '}';
   }
}
