# -*- coding: utf-8 -*-
"""
Sphinx plugins for Riot documentation.
"""
import re
import sphinx
from docutils import nodes
from sphinx.highlighting import lexers
from freemarker import FreeMarkerHtmlLexer

apirefs = {
    'riot': 'http://www.riotfamily.org/static/docs/9.1.x/api',
    'spring': 'http://static.springsource.org/spring/docs/3.0.x/javadoc-api'
}

def setup(app):
    app.add_role('api', api_reference_role)
    lexers['ftl'] = FreeMarkerHtmlLexer()

    
def api_reference_role(role, rawtext, text, lineno, inliner, options={}, content=[]):
    
    #m = re.search('^(?:(.+?)\s*<)?(.+\.(.+))(?:#(\S+))?\s*>?$', text)
    #(label, fqn, classname, method) = m.group(1, 2, 3, 4)
    
    m = re.search('^(.*\.(.*?))(?:#(\S*))?(?:\s(.*))?$', text)
    (fqn, classname, method, label) = m.group(1, 2, 3, 4)
    
    path = fqn.replace('.', '/').replace('@', '')
    hash = ''
    if method:
        hash = '#' + method
        classname += '.' + method
    
    for lib, uri in apirefs.iteritems():
        if fqn.find(lib) != -1:
            ref = '%s/index.html?%s%s' % (uri, path, hash)
            node = nodes.reference(rawtext, label or classname, refuri=ref, **options)
            node['classes'] += ['api', lib]
            return [node], []
