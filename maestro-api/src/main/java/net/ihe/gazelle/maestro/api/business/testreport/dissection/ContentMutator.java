package net.ihe.gazelle.maestro.api.business.testreport.dissection;

/**
 * Functional interface allowing a dissector to adjust an item's content once
 * referenced item/attachment identifiers are known.
 */
@FunctionalInterface
public interface ContentMutator {

   /**
    * @param context  exposes resolved references gathered during persistence
    * @return updated content to persist (must not be {@code null})
    */
   String mutate(String content, ContentMutationContext context);
}
