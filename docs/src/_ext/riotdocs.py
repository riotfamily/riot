# -*- coding: utf-8 -*-
"""
Sphinx plugins for Riot documentation.
"""

import sphinx
import re
from docutils import nodes

apirefs = {
    'org.springframework': 'http://static.springsource.org/spring/docs/3.0.x/javadoc-api',
    'org.riotfamily': 'http://riotfamily.org/api/9.0.x'
}

def setup(app):
    app.add_role('api', api_reference_role)
    
def api_reference_role(role, rawtext, text, lineno, inliner, options={}, content=[]):
    
    m = re.search('^(.*\.(.*))(?:#(.*))?$', text)
    (fqn, classname, method) = m.group(1, 2, 3)
    text = classname
    path = fqn.replace('.', '/').replace('@', '')
    hash = ''
    if method:
        hash = '#' + method
        classname += '.' + method
    
    for package, uri in apirefs.iteritems():
        if fqn.startswith(package):
            ref = '%s/index.html?%s%s' % (uri, path, hash)
            node = nodes.reference(rawtext, classname, refuri=ref, **options)
            return [node], []


