/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ubl.iiif.dynamic.webanno;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.ubl.iiif.dynamic.webanno.Constants.dynamoBase;
import static org.ubl.iiif.dynamic.webanno.Constants.presentationContext;
import static org.ubl.iiif.dynamic.webanno.Constants.scManifestType;
import static org.ubl.iiif.dynamic.webanno.Constants.trellisCollectionBase;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CollectionBuilder.
 *
 * @author christopher-johnson
 */
public class CollectionBuilder extends AbstractSerializer {

    private final String body;

    /**
     * @param body String
     */
    public CollectionBuilder(final String body) {
        this.body = body;
    }

    /**
     * @return List
     */
    public List<Canvas> readBody() {
        try {
            final Targets targets = MAPPER.readValue(body, new TypeReference<Targets>() {
            });
            return targets.getGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param graph graph
     * @return List
     */
    public List<CollectionNode> getCollectionNodes(final List<Canvas> graph) {
        final List<CollectionNode> collectionNodes = new ArrayList<>();
        final List<String> values = new ArrayList<>();
        graph.forEach(c -> {
            final List<Metadata> metadataList = c.getMetadata();
            metadataList.forEach(m -> {
                values.add(m.getValue());
            });
        });
        final List<String> uniqueValues = values.stream().distinct().collect(Collectors.toList());
        uniqueValues.forEach(v -> {
            final CollectionNode node = new CollectionNode();
            node.setLabel(v);
            try {
                node.setId(dynamoBase + URLEncoder.encode(v, UTF_8.toString()) + "&v2=");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            node.setType(scManifestType);
            collectionNodes.add(node);
        });
        collectionNodes.sort(Comparator.comparing(CollectionNode::getLabel));
        return collectionNodes;
    }

    /**
     * @param collectionNodes collectionNodes
     * @return Collection
     */
    public Collection getCollection(final List<CollectionNode> collectionNodes) {
        final String id = trellisCollectionBase + UUID.randomUUID();

        final Collection collection = new Collection();
        collection.setContext(presentationContext);
        collection.setId(id);
        collection.setCollectionNodes(collectionNodes);
        return collection;
    }

    /**
     * @return String
     */
    public String build() {
        final List<Canvas> body = readBody();
        final List<CollectionNode> collectionNodes = getCollectionNodes(body);
        final Collection collection = getCollection(collectionNodes);
        final Optional<String> json = serialize(collection);
        return json.orElse(null);
    }
}
