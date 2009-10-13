/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.riotfamily.search.site;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.search.SearchController;

/**
 * SearchController subclass that filters the search search result to show
 * only hits from the same site.  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteSearchController extends SearchController {

    protected Query createFilterQuery(HttpServletRequest request) {
        Query query = null;
        
        Page currentPage = PageResolver.getResolvedPage(request);
        if (currentPage != null) {
            String siteId = currentPage.getSite().getId().toString();
            
            query = new TermQuery(new Term("siteId", siteId));
        }
        
        return combineFilterQueries(query, super.createFilterQuery(request));
    }
    
    private Query combineFilterQueries(Query q, Query r) {
        if (q == null && r == null) return null;
        if (q == null) return r;
        if (r == null) return q;

        BooleanQuery query = new BooleanQuery();
        query.add(q, Occur.MUST);
        query.add(r, Occur.MUST);
        return query;
    }
}
