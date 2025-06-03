package net.ihe.gazelle.technical.ws;

import jakarta.ws.rs.core.Response;
import net.ihe.gazelle.mlang.converter.*;

import java.io.*;


public class ConverterControllerImpl implements ConverterController {

    Converter jsonConverter = new MLangJSONConverter();
    Converter mlangConverter = new JSONMLangConverter();

    @Override
    public Response convertToMlang(InputStream inputStream) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(output)) {

            mlangConverter.convert(new java.io.InputStreamReader(inputStream), writer);
            return Response.ok().entity(output.toString()).build();
        } catch (ConverterException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (ParserException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid MLANG format: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred").build();
        }
    }

    @Override
    public Response convertToJson(InputStream inputStream) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(output)) {

            jsonConverter.convert(new java.io.InputStreamReader(inputStream), writer);
            return Response.ok().entity(output.toString()).build();
        } catch (ConverterException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (ParserException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid JSON format: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An unexpected error occurred").build();
        }
    }

}
