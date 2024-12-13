package net.ihe.gazelle.testreport;

import java.util.Objects;

public class UnexpectedError {

   private String name;
   private String message;
   private UnexpectedError cause;

   public UnexpectedError() {
   }

   public UnexpectedError(UnexpectedError other) {
      this();
      setName(other.getName());
      setMessage(other.getMessage());
      setCause(other.getCause() != null ? new UnexpectedError(other.getCause()) : null);
   }

   public UnexpectedError(Throwable t) {
      name = t.getClass().getSimpleName();
      message = t.getMessage();
      cause = t.getCause() != null ? new UnexpectedError(t.getCause()) : null;
   }

   public String getName() {
      return name;
   }

   public UnexpectedError setName(String name) {
      this.name = name;
      return this;
   }

   public String getMessage() {
      return message;
   }

   public UnexpectedError setMessage(String message) {
      this.message = message;
      return this;
   }

   public UnexpectedError getCause() {
      return cause;
   }

   public UnexpectedError setCause(UnexpectedError cause) {
      this.cause = cause;
      return this;
   }

   public boolean isNameValid() {
      return name != null && !name.isEmpty();
   }

   public boolean isMessageValid() {
      return message != null && !message.isBlank();
   }

   public boolean isValid() {
      return isNameValid() && isMessageValid();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof UnexpectedError that)) return false;
      return Objects.equals(name, that.name)
              && Objects.equals(message, that.message)
              && Objects.equals(cause, that.cause);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, message, cause);
   }
}
