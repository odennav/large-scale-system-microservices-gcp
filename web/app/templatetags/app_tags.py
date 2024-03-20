from django import template
import json
import logging

AUTH_USER_COOKIE = 'user_auth'
register = template.Library()

logger = logging.getLogger(__name__)


@register.inclusion_tag('app/header.html', takes_context=True)
def include_header(context):
    request = context['request']
    if AUTH_USER_COOKIE in request.COOKIES:
        user_auth = request.COOKIES[AUTH_USER_COOKIE]
        logger.debug('Read UserAuth from cookie | context=%s', user_auth)
        user_name = json.loads(user_auth)['name']
        return {'user_name': user_name}
    return {'user_name': ''}

