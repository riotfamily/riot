# -*- coding: utf-8 -*-
"""
Sphinx plugins for Riot documentation.
"""

import sphinx
import re
from docutils import nodes

apirefs = {
    'riot': 'http://riotfamily.org/api/9.0.x',
    'spring': 'http://static.springsource.org/spring/docs/3.0.x/javadoc-api'
}

def setup(app):
    app.add_role('api', api_reference_role)
    
def api_reference_role(role, rawtext, text, lineno, inliner, options={}, content=[]):
    
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


