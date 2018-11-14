package com.github.malow.FantasyEsports.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.malow.FantasyEsports.services.HttpResponseException.MissingMandatoryFieldException;
import com.github.malow.malowlib.MaloWLogger;

public abstract class Request
{
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public static @interface Mandatory
  {
  }

  public void validate() throws MissingMandatoryFieldException
  {
    List<Field> fields = Arrays.asList(this.getClass().getFields());
    fields = fields.stream().filter(f -> f.isAnnotationPresent(Mandatory.class)).collect(Collectors.toList());
    for (Field field : fields)
    {
      try
      {
        Object o = field.get(this);
        if (o == null)
        {
          throw new MissingMandatoryFieldException(field.getName());
        }
        if (field.getType().equals(String.class) && ((String) o).isEmpty())
        {
          throw new MissingMandatoryFieldException(field.getName());
        }
      }
      catch (MissingMandatoryFieldException e)
      {
        throw e;
      }
      catch (Exception e)
      {
        MaloWLogger.error("Failed to check mandantory field of class " + this.getClass().getSimpleName(), e);
      }
    }
    this.validateSpecific();
  }

  protected void validateSpecific()
  {

  }
}
